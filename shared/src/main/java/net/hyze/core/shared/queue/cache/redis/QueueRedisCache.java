package net.hyze.core.shared.queue.cache.redis;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class QueueRedisCache implements RedisCache {

    public static final Function<Server, String> KEY_CONVERTER = server -> "queue:" + server.getId();

    public Set<Integer> range(Server server, int limit) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Set<String> strIds = jedis.zrange(KEY_CONVERTER.apply(server), 0, limit);

            return strIds.stream().map(Integer::valueOf).collect(Collectors.toSet());
        }
    }

    public void add(User user, Server server) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            if (user.hasGroup(Group.VIP)) {
                pipeline.zadd(KEY_CONVERTER.apply(server), 0, String.valueOf(user.getId()));
            } else {
                pipeline.zadd(
                        KEY_CONVERTER.apply(server),
                        Long.valueOf(System.currentTimeMillis()).doubleValue(),
                        String.valueOf(user.getId())
                );
            }

            CoreProvider.Cache.Redis.USERS_STATUS.provide().setQueue(pipeline, user, server);

            pipeline.sync();
        }
    }

    public long position(User user, Server server) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.zrank(KEY_CONVERTER.apply(server), String.valueOf(user.getId()));
        }
    }

    public void remove(User user) {

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            for (Server server : Server.values()) {
                pipeline.zrem(KEY_CONVERTER.apply(server), String.valueOf(user.getId()));
            }

            CoreProvider.Cache.Redis.USERS_STATUS.provide().removeQueue(pipeline, user);

            pipeline.sync();
        }
    }
}
