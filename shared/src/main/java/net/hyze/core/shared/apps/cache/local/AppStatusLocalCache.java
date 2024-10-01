package net.hyze.core.shared.apps.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.cache.local.LocalCache;

import java.util.concurrent.TimeUnit;

public class AppStatusLocalCache<T extends AppStatus> implements LocalCache {

    private final Cache<String, T> CACHE_BY_ID = Caffeine.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build();

    public T fetch(App app, Class<T> statusClass) {
        return CACHE_BY_ID.get(app.getId(), id -> {
            return (T) CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(id, statusClass);
        });
    }

    public T fetch(String appId, Class<T> statusClass) {
        return CACHE_BY_ID.get(appId, id -> {
            return (T) CoreProvider.Cache.Redis.APPS_STATUS.provide().fetch(id, statusClass);
        });
    }
}
