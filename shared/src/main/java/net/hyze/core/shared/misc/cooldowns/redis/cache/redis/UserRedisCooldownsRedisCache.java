package net.hyze.core.shared.misc.cooldowns.redis.cache.redis;

import com.google.common.primitives.Longs;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.redis.RedisCache;
import net.hyze.core.shared.user.User;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

public class UserRedisCooldownsRedisCache implements RedisCache {

    private static final Function<User, String> KEY_CONVERTER = user -> "users_cooldowns:" + user.getId();

    public void start(User user, String key, long expires) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hset(KEY_CONVERTER.apply(user), key, String.valueOf(expires));
        }
    }

    public Long get(User user, String key) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            String value = jedis.hget(KEY_CONVERTER.apply(user), key);

            if (value != null) {
                return Longs.tryParse(value);
            }

            return null;
        }
    }

    public void end(User user, String key) {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            jedis.hdel(KEY_CONVERTER.apply(user), key);
        }
    }
}
