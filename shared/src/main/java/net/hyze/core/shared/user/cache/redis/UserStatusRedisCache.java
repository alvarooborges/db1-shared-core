package net.hyze.core.shared.user.cache.redis;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.misc.utils.DateUtils;
import net.hyze.core.shared.redis.RedisScriptManager;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserStatusRedisCache implements RedisCache {

    public static final int TTL_SECONDS = 10;

    public static final Function<String, String> KEY_CONVERTER_BY_NICK = nick -> "users:" + nick.toLowerCase();

    public static final BiConsumer<String, Map<String, String>> UPDATE_CONSUMER = (nick, map) -> {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.hmset(KEY_CONVERTER_BY_NICK.apply(nick), map);
            pipeline.expire(KEY_CONVERTER_BY_NICK.apply(nick), TTL_SECONDS);
            pipeline.sync();
        }
    };

    public void ping(Iterable<String> nicks) {
        if (!Iterables.isEmpty(nicks)) {
            try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
                Pipeline pipeline = jedis.pipelined();

                for (String nick : nicks) {
                    pipeline.expire(KEY_CONVERTER_BY_NICK.apply(nick), TTL_SECONDS);
                }

                pipeline.sync();
            }
        }
    }

    public void initialize(@NonNull String nick, InetSocketAddress ip, int version, Double hyzeVersion, Double hyzeApiVersion, String hardwareId, Date joinedAt) {

        Preconditions.checkState(CoreProvider.getApp().getType() == AppType.PROXY, "Only proxies can initialize user status.");

        Map<String, String> map = Maps.newHashMap();

        map.put("nick", nick);
        map.put("proxy_app", CoreProvider.getApp().getId());
        map.put("ip", ip.getAddress().getHostAddress());
        map.put("version", String.valueOf(version));

        if(hyzeVersion != null) {
            map.put("hyze_client_version", hyzeVersion.toString());
            map.put("hyze_client_api_version", hyzeApiVersion.toString());
            map.put("hardware_id", hardwareId);
        }

        map.put("joined_at", DateUtils.toString(joinedAt));

        UPDATE_CONSUMER.accept(nick, map);

    }

    public boolean exists(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.exists(KEY_CONVERTER_BY_NICK.apply(nick));
        }
    }

    public void delete(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.del(KEY_CONVERTER_BY_NICK.apply(nick));
        }
    }

    public void setLogged(@NonNull User user) {

        Map<String, String> map = Maps.newHashMap();

        map.put("logged_at", DateUtils.toString(new Date()));
        map.put("id", String.valueOf(user.getId()));

        UPDATE_CONSUMER.accept(user.getNick(), map);

    }

    public boolean isLogged(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.hexists(KEY_CONVERTER_BY_NICK.apply(nick), "logged_at");
        }
    }

    public void setProxyApp(@NonNull String nick, @NonNull App app) {

        Map<String, String> map = Maps.newHashMap();

        map.put("proxy_app", app.getId());

        UPDATE_CONSUMER.accept(nick, map);

    }

    public App getProxyApp(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String appId = jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "proxy_app");

            return CoreProvider.Cache.Local.APPS.provide().get(appId);
        }
    }

    public Response<String> getProxy(@NonNull String nick, @NonNull Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "proxy_app");
    }

    public Server getQueue(User user) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return Server.getById(jedis.hget(KEY_CONVERTER_BY_NICK.apply(user.getNick()), "queue")).orNull();
        }
    }

    public void setQueue(Pipeline pipeline, User user, Server server) {
        Map<String, String> map = Maps.newHashMap();

        map.put("queue", server.getId());

        pipeline.hmset(KEY_CONVERTER_BY_NICK.apply(user.getNick()), map);
        pipeline.expire(KEY_CONVERTER_BY_NICK.apply(user.getNick()), TTL_SECONDS);
    }

    public void removeQueue(Pipeline pipeline, User user) {
        pipeline.hdel(KEY_CONVERTER_BY_NICK.apply(user.getNick()), "queue");
    }

    public void setBukkitApp(@NonNull String nick, @NonNull App app) {
        setBukkitApp(nick, app.getId());
    }

    public void setBukkitApp(@NonNull String nick, @NonNull String appId) {

        Map<String, String> map = Maps.newHashMap();

        map.put("bukkit_app", appId);

        UPDATE_CONSUMER.accept(nick, map);

    }

    public String getBukkitApp_(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "bukkit_app");
        }
    }

    public App getBukkitApp(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String appId = jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "bukkit_app");

            if (appId != null) {
                return CoreProvider.Cache.Local.APPS.provide().get(appId);
            }
        }

        return null;
    }

    public Response<String> getBukkitApp(@NonNull String nick, @NonNull Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "bukkit_app");
    }

    public Date getJoinedAt(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String rawDate = jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "joined_at");
            return DateUtils.fromString(rawDate);
        }
    }

    public Response<String> getJoinedAt(@NonNull String nick, @NonNull Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "joined_at");
    }

    public String getIp(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "ip");
        }
    }

    public Response<String> getIp(@NonNull String nick, @NonNull Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "ip");
    }

    //
    public Integer getVersion(@NonNull String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return Ints.tryParse(jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "version"));
        }
    }

    public Response<String> getVersion(@NonNull String nick, @NonNull Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "version");
    }

    public Map<User, AppStatus> getBukkitApp(Set<User> users) {
        Map<User, AppStatus> out = Maps.newHashMap();

        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Pipeline pipeline = jedis.pipelined();

            Map<User, Response<String>> map = Maps.newHashMap();

            users.forEach(user -> {
                Response<String> response = getBukkitApp(user.getNick(), pipeline);

                map.put(user, response);
            });

            pipeline.sync();

            Set<String> appsIds = Sets.newHashSet();

            map.values().forEach(response -> {
                String appId = response.get();

                if (appId != null) {
                    appsIds.add(appId);
                }
            });

            Map<String, AppStatus> statuses = CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(appsIds);

            map.forEach((user, response) -> {
                out.put(user, statuses.get(response.get()));
            });
        }

        return out;
    }

//    public void addUserInProxy(String proxyId, int... users) {
//        String[] proxyUsersId = Arrays.stream(users)
//                .mapToObj(String::valueOf)
//                .toArray(String[]::new);
//
//        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
//            jedis.sadd("proxies:" + proxyId + ":users", proxyUsersId);
//        }
//    }
//
//    public void addUserInServer(String serverId, int... users) {
//        String[] serverUsersId = Arrays.stream(users)
//                .mapToObj(String::valueOf)
//                .toArray(String[]::new);
//
//        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
//            jedis.sadd("servers:" + serverId + ":users", serverUsersId);
//        }
//    }
//
//    public void removeUserFromProxy(String proxyId, int... users) {
//        String[] proxyUsersId = Arrays.stream(users)
//                .mapToObj(String::valueOf)
//                .toArray(String[]::new);
//
//        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
//            jedis.srem("proxies:" + proxyId + ":users", proxyUsersId);
//        }
//    }
//
//    public void removeUserFromServer(String serverId, int... users) {
//        String[] serverUsersId = Arrays.stream(users)
//                .mapToObj(String::valueOf)
//                .toArray(String[]::new);
//
//        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
//            jedis.srem("servers:" + serverId + ":users", serverUsersId);
//        }
//    }
    public Set<User> fetchUsersByServer(Server server) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Object object = RedisScriptManager.execute(
                    jedis,
                    "fetchUsersByServer.lua",
                    Collections.emptyList(),
                    Lists.newArrayList(server.name())
            );

            if (object != null) {
                return ((Collection<String>) object).stream()
                        .map(Integer::valueOf)
                        .map(CoreProvider.Cache.Local.USERS.provide()::get)
                        .collect(Collectors.toSet());
            }

            return Collections.emptySet();
        }
    }

    public void incrIpRegistrationAmount(String ip) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.incr(String.format("ip:registration:%s", ip));
        }
    }

    public int getIpRegistrationAmount(String ip) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String value = jedis.get(String.format("ip:registration:%s", ip));

            if (value != null) {
                Integer amount = Ints.tryParse(value);

                if (amount != null) {
                    return amount;
                }
            }

            return 0;
        }
    }

    public Double getHyzeClientVersion(String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String s = jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "hyze_client_version");
            return s == null ? null : Doubles.tryParse(s);
        }
    }

    public Response<String> getHyzeClientVersion(String nick, Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "hyze_client_version");
    }

    public Double getHyzeClientApiVersion(String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String s = jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "hyze_client_api_version");
            return s == null ? null : Doubles.tryParse(s);
        }
    }

    public Response<String> getHyzeClientApiVersion(String nick, Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "hyze_client_api_version");
    }

    public String getHardwareId(String nick) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.hget(KEY_CONVERTER_BY_NICK.apply(nick), "hardware_id");
        }
    }

    public Response<String> getHardwareId(String nick, Pipeline pipeline) {
        return pipeline.hget(KEY_CONVERTER_BY_NICK.apply(nick), "hardware_id");
    }
}
