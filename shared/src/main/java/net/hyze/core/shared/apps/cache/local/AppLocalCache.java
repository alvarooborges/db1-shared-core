package net.hyze.core.shared.apps.cache.local;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.cache.local.LocalCache;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;

public class AppLocalCache implements LocalCache {

    private final LoadingCache<String, App> CACHE_BY_ID = Caffeine.newBuilder().build((String id) -> {
        return CoreProvider.Repositories.APPS.provide().fetchById(id);
    });

    public App getIfPresent(@NonNull String id) {
        return CACHE_BY_ID.getIfPresent(id);
    }

    public App get(@NonNull String id) {
        return CACHE_BY_ID.get(id);
    }

    public App refresh(@NonNull String id) {
        CACHE_BY_ID.refresh(id);

        return getIfPresent(id);
    }

    public void put(@NonNull App app) {
        CACHE_BY_ID.put(app.getId(), app);
    }

    public List<App> get(AppType type) {
        return CACHE_BY_ID.asMap().values().stream()
                .filter(app -> app.getType() == type)
                .collect(Collectors.toList());
    }

    public List<App> get() {
        return CACHE_BY_ID.asMap().values().stream().collect(Collectors.toList());
    }
}
