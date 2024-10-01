package net.hyze.core.shared.user.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.cache.local.LocalCache;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UserStatusLocalCache implements LocalCache {

    protected final Cache<String, Map<String, String>> CACHE_BY_ID = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    private String get(String nick, String key) {
        Map<String, String> map = CACHE_BY_ID.getIfPresent(nick);

        if (map != null) {
            return map.get(key);
        }

        return null;
    }

    private void put(String nick, String key, String value) {
        Map<String, String> map = CACHE_BY_ID.getIfPresent(nick);

        if (map == null) {
            map = Maps.newHashMap();
        }

        map.put(key, value);

        CACHE_BY_ID.put(nick, map);
    }

    public String getBukkitApp_(@NonNull String nick) {
        String appId = get(nick, "bukkit_app");
        if(appId != null) {
            return appId;
        }

        appId = CoreProvider.Cache.Redis.USERS_STATUS.provide().getBukkitApp_(nick);

        if (appId != null) {
            put(nick, "bukkit_app", appId);
        }

        return appId;
    }

    public App getBukkitApp(@NonNull String nick) {

        String appId = get(nick, "bukkit_app");

        if (appId != null) {
            return CoreProvider.Cache.Local.APPS.provide().get(appId);
        }

        App app = CoreProvider.Cache.Redis.USERS_STATUS.provide().getBukkitApp(nick);

        if (app != null) {
            put(nick, "bukkit_app", app.getId());

            return app;
        }

        return null;
    }
}
