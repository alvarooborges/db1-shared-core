package net.hyze.core.shared.misc.cooldowns.redis.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.user.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class UserRedisCooldownsLocalCache implements LocalCache {

    private final Map<User, LoadingCache<String, Optional<Long>>> CACHE = Maps.newConcurrentMap();

    public void start(User user, String key, long expires) {
        LoadingCache<String, Optional<Long>> cache = getCache(user);

        cache.put(key, Optional.ofNullable(expires));
        CoreProvider.Cache.Redis.USER_REDIS_COOLDOWNS
                .provide()
                .start(user, key, expires);
    }

    public Long get(User user, String key) {
        LoadingCache<String, Optional<Long>> cache = getCache(user);

        return cache.get(key).orElse(null);
    }

    public void end(User user, String key) {
        LoadingCache<String, Optional<Long>> cache = getCache(user);

        cache.invalidate(key);

        CoreProvider.Cache.Redis.USER_REDIS_COOLDOWNS
                .provide()
                .end(user, key);
    }

    private LoadingCache<String, Optional<Long>> getCache(User user) {
        LoadingCache<String, Optional<Long>> cache = CACHE.get(user);

        if (cache == null) {
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(3, TimeUnit.SECONDS)
                    .build(key -> {
                        Long expires = CoreProvider.Cache.Redis.USER_REDIS_COOLDOWNS
                                .provide()
                                .get(user, key);

                        return Optional.ofNullable(expires);
                    });
        }

        return cache;
    }
}
