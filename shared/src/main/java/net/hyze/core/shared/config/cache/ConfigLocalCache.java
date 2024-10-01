package net.hyze.core.shared.config.cache;

import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.misc.utils.Printer;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class ConfigLocalCache implements LocalCache {

    private final ConcurrentMap<String, String> configuration = Maps.newConcurrentMap();

    public String getRaw(String key) {
        return this.configuration.get(key);
    }

    public <T> T get(String key, Class<T> elementType) {
        return get(key, elementType, null);
    }
    
    public <T> T get(String key, Class<T> elementType, T defaultValue) {
        String json = this.configuration.get(key);

        if (json != null) {
            try {
                return CoreConstants.GSON.fromJson(json, elementType);
            } catch (JsonSyntaxException exception) {
                Printer.INFO.coloredPrint("&e[Config] Config '%s' is not a valid JSON. Default value returned.");
                exception.printStackTrace();
                return defaultValue;
            }
        }

        return defaultValue;
        
    }

    public synchronized void update(Map<String, String> map) {

        this.configuration.keySet().stream()
                .filter(key -> !map.containsKey(key))
                .forEach((key) -> {
                    this.configuration.remove(key);
                });

        this.configuration.putAll(map);

    }

    @Override
    public void populate() {
        this.update(CoreProvider.Repositories.CONFIG.provide().fetch());
    }

}
