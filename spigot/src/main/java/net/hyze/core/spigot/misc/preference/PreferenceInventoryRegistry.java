package net.hyze.core.spigot.misc.preference;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.user.preferences.PreferenceStatus;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.LinkedHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PreferenceInventoryRegistry {

    private static final LinkedHashMap<String, ImmutablePair<PreferenceIcon, PreferenceStatus>> MAP = Maps.newLinkedHashMap();

    public static void registry(String id, PreferenceIcon icon, PreferenceStatus defaultStatus) {
        MAP.put(id, new ImmutablePair<>(icon, defaultStatus));
    }

    public static LinkedHashMap<String, ImmutablePair<PreferenceIcon, PreferenceStatus>> get() {
        return MAP;
    }
}
