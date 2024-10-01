package net.hyze.core.spigot.misc.tags.utils;

import net.hyze.core.spigot.misc.tags.api.TagData;

import java.util.concurrent.ConcurrentHashMap;

public class TagDataMap extends ConcurrentHashMap<String, TagData> {

    @Override
    public TagData get(Object key) {
        if (key instanceof String) {
            key = Utils.parseName(((String) key));
        }
        return super.get(key);
    }

    @Override
    public TagData remove(Object key) {
        if (key instanceof String) {
            key = Utils.parseName(((String) key));
        }
        return super.remove(key);
    }
}
