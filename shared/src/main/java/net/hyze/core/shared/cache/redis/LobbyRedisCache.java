package net.hyze.core.shared.cache.redis;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppStatus;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LobbyRedisCache implements RedisCache {

    private LoadingCache<String, AppStatus> CACHE = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build(id -> {
                return CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(id, AppStatus.class);
            });

    public Map<String, AppStatus> fetchAll() {
        try (Jedis jedis = CoreProvider.Redis.REDIS_MAIN.provide().getResource()) {
            Set<String> keys = jedis.keys("apps:lobby-*");

            Map<String, AppStatus> out = Maps.newHashMap();

            Set<String> toFetch = Sets.newHashSet();

            keys.forEach(key -> {
                AppStatus status;
                if ((status = CACHE.getIfPresent(key)) != null) {
                    out.put(key, status);
                } else {
                    toFetch.add(key);
                }
            });

            if (!toFetch.isEmpty()) {
                Map<String, AppStatus> response = CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(toFetch);

                List<AppStatus> list = response.values().stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                list.forEach((status) -> {
                    CACHE.put(status.getAppId(), status);
                    out.put(status.getAppId(), status);
                });
            }

            return out;
        }
    }
}
