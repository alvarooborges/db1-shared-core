package net.hyze.core.shared.settings.cache.redis;

import java.util.Map;
import java.util.function.Function;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.servers.Server;
import redis.clients.jedis.Jedis;

public class SettingsRedisCache implements RedisCache {

    public static final Function<Server, String> KEY_CONVERTER_BY_SERVER = server -> {
        return "settings:server:" + server.getId();
    };

    public Map<String, String> fetchSettingsByServer(Server server) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            return jedis.hgetAll(KEY_CONVERTER_BY_SERVER.apply(server));
        }
    }

    public void setSettingsByServer(Server server, String key, String value) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hset(KEY_CONVERTER_BY_SERVER.apply(server), key, value);
        }
    }
}
