package net.hyze.core.shared.dungeon.cache.local;

import com.google.common.collect.Maps;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.dungeon.BasicDungeonMap;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;

public class DungeonMapLocalCache<DM extends BasicDungeonMap> implements LocalCache {

    private final Map<String, DM> CACHE_BY_ID = Maps.newHashMap();

    public DM get(@NonNull String id) {
        return CACHE_BY_ID.get(id);
    }

    public void put(@NonNull DM map) {
        CACHE_BY_ID.put(map.getId(), map);
    }

    public void remove(@NonNull DM map) {
        CACHE_BY_ID.remove(map.getId());
    }

    public Collection<DM> get() {
        return CACHE_BY_ID.values();
    }

}
