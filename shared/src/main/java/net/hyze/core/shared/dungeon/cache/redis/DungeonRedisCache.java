package net.hyze.core.shared.dungeon.cache.redis;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.dungeon.BasicDungeonMap;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.core.shared.providers.RedisProvider;
import net.hyze.core.shared.redis.RedisScriptManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.*;

@RequiredArgsConstructor
public class DungeonRedisCache implements RedisCache {

    private static final String DUNGEONS_KEY = "dungeons";
    private static final String DUNGEONS_ID_KEY = "dungeons:%s";
    private static final String DUNGEONS_PENDING_KEY = "dungeonspending:%s";
    private static final String DUNGEONS_READY_KEY = "dungeonsready:%s";
    private static final String DUNGEONS_READYCD_KEY = "dungeonsreadycd:%s";
    private static final String DUNGEONS_SLAVES_KEY = "dungeonslaves";
    private static final String DUNGEONS_SLAVES_ID_KEY = "dungeonslaves:%s";

    private final RedisProvider redis;

    public int getAmountOfServers() {
        try (Jedis jedis = redis.provide().getResource()) {
            return jedis.scard(DUNGEONS_KEY).intValue();
        }
    }

    public Collection<String> cleanErroredServers(BasicDungeonMap map) {
        try (Jedis jedis = redis.provide().getResource()) {
            Collection<String> readyIds = jedis.smembers(String.format(DUNGEONS_READY_KEY, map.getId()));

            if(!readyIds.isEmpty()) {
                Collection<AppStatus> statuses = CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(readyIds)
                    .values();

                statuses.stream()
                    .filter(Objects::nonNull)
                    .map(AppStatus::getAppId)
                    .filter(Objects::nonNull)
                    .forEach(readyIds::remove);

                if (!readyIds.isEmpty()) {
                    destroyServers(readyIds);
                }
            }

            return readyIds;
        }
    }

    public int getAmountOfServers(BasicDungeonMap map) {
        try (Jedis jedis = redis.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            Response<Long> pend = pipeline.scard(String.format(DUNGEONS_PENDING_KEY, map.getId()));
            Response<Long> ready = pipeline.scard(String.format(DUNGEONS_READY_KEY, map.getId()));
            Response<Long> readycd = pipeline.scard(String.format(DUNGEONS_READYCD_KEY, map.getId()));
            pipeline.sync();

            return (int) (pend.get() + ready.get() + readycd.get());
        }
    }

    public int getNextDungeonId() {
        try (Jedis jedis = redis.provide().getResource()) {
            Long id = jedis.incr("dungeonid");

            if (id > 500) {
                jedis.set("dungeonid", "1");
                return 1;
            }

            return id.intValue();
        }
    }

    public Set<String> getAllDungeons() {
        try (Jedis jedis = redis.provide().getResource()) {
            return jedis.smembers(DUNGEONS_KEY);
        }
    }

    public Set<String> getReadyDungeons(String mapId) {
        try (Jedis jedis = redis.provide().getResource()) {
            return jedis.smembers(String.format(DUNGEONS_READY_KEY, mapId));
        }
    }

    public Set<String> getSlaves() {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.smembers(DUNGEONS_SLAVES_KEY);
        }
    }

    //

    public void addSlave(String slaveId) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.sadd(DUNGEONS_SLAVES_KEY, slaveId);
        }
    }

    public void removeSlave(String slaveId) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.srem(DUNGEONS_SLAVES_KEY, slaveId);
        }
    }

    public Set<String> getSlaveServers(String slaveId) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.smembers(String.format(DUNGEONS_SLAVES_ID_KEY, slaveId));
        }
    }

    public String getLightestSlave() {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return Optional.ofNullable(RedisScriptManager.execute(jedis, "dungeon/fetchLightestSlave.lua", Collections.emptyList(), Collections.emptyList())).map(String::valueOf).orElse(null);
        }
    }

    public void initializeServer(String id, String slaveId, String mapId) {
        try (Jedis jedis = redis.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            pipeline.sadd(DUNGEONS_KEY, id);
            pipeline.sadd(String.format(DUNGEONS_PENDING_KEY, mapId), id);
            pipeline.hset(String.format(DUNGEONS_ID_KEY, id), "slave", slaveId);
            pipeline.hset(String.format(DUNGEONS_ID_KEY, id), "map", mapId);

            pipeline.sync();
        }

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.sadd(String.format(DUNGEONS_SLAVES_ID_KEY, slaveId), id);
        }
    }

    public void destroyServers(Collection<String> ids) {
        try (Jedis jedis = redis.provide().getResource()) {
            Collection<Runnable> runs = Sets.newHashSet();

            Pipeline pipeline = jedis.pipelined();

            for (String id : ids) {
                Response<String> slave = pipeline.hget(String.format(DUNGEONS_ID_KEY, id), "slave");
                Response<String> map = pipeline.hget(String.format(DUNGEONS_ID_KEY, id), "map");

                runs.add(() -> {
                    destroyServer0(jedis, id, slave.get(), map.get());
                });
            }

            pipeline.sync();
            runs.forEach(Runnable::run);
        }
    }

    public void destroyServer(String id) {
        try (Jedis jedis = redis.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            Response<String> slave = pipeline.hget(String.format(DUNGEONS_ID_KEY, id), "slave");
            Response<String> map = pipeline.hget(String.format(DUNGEONS_ID_KEY, id), "map");

            pipeline.sync();

            destroyServer0(jedis, id, slave.get(), map.get());
        }
    }

    public void destroyServer(String id, String slaveId, String mapId) {
        try (Jedis jedis = redis.provide().getResource()) {
            destroyServer0(jedis, id, slaveId, mapId);
        }
    }

    private void destroyServer0(Jedis jedis, String id, String slaveId, String mapId) {
        Pipeline pipeline = jedis.pipelined();

        pipeline.srem(DUNGEONS_KEY, id);

        if (slaveId != null) {
            try (Jedis jedis_ = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
                jedis_.srem(String.format(DUNGEONS_SLAVES_ID_KEY, slaveId), id);
            }
        }

        if (mapId != null) {
            pipeline.srem(String.format(DUNGEONS_PENDING_KEY, mapId), id);
            pipeline.srem(String.format(DUNGEONS_READY_KEY, mapId), id);
            pipeline.srem(String.format(DUNGEONS_READYCD_KEY, mapId), id);
        }

        pipeline.del(String.format(DUNGEONS_ID_KEY, id));

        pipeline.sync();
    }

    /*

     */

    public String fetchReadyServer(BasicDungeonMap map) {
        try (Jedis jedis = redis.provide().getResource()) {
            return jedis.srandmember(String.format(DUNGEONS_READY_KEY, map.getId()));
        }
    }

    public void checkReadyCD(BasicDungeonMap dungeonMap, String appId) {
        try (Jedis jedis = redis.provide().getResource()) {
            if (jedis.sismember(String.format(DUNGEONS_READYCD_KEY, dungeonMap.getId()), appId)) {
                Pipeline pipeline = jedis.pipelined();
                pipeline.srem(String.format(DUNGEONS_READYCD_KEY, dungeonMap.getId()), appId);
                pipeline.sadd(String.format(DUNGEONS_READY_KEY, dungeonMap.getId()), appId);
                pipeline.sync();
            }
        }
    }

    public void markAsReadyCD(BasicDungeonMap dungeonMap, String appId) {
        try (Jedis jedis = redis.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.srem(String.format(DUNGEONS_PENDING_KEY, dungeonMap.getId()), appId);
            pipeline.srem(String.format(DUNGEONS_READY_KEY, dungeonMap.getId()), appId);
            pipeline.sadd(String.format(DUNGEONS_READYCD_KEY, dungeonMap.getId()), appId);
            pipeline.sync();
        }
    }

    public void markAsReady(BasicDungeonMap dungeonMap, String appId) {
        try (Jedis jedis = redis.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.srem(String.format(DUNGEONS_PENDING_KEY, dungeonMap.getId()), appId);
            pipeline.srem(String.format(DUNGEONS_READYCD_KEY, dungeonMap.getId()), appId);
            pipeline.sadd(String.format(DUNGEONS_READY_KEY, dungeonMap.getId()), appId);
            pipeline.sync();
        }
    }

    public void markAsRunning(BasicDungeonMap dungeonMap, String appId) {
        try (Jedis jedis = redis.provide().getResource()) {
            jedis.srem(String.format(DUNGEONS_READY_KEY, dungeonMap.getId()), appId);
            jedis.srem(String.format(DUNGEONS_READYCD_KEY, dungeonMap.getId()), appId);
        }
    }
}
