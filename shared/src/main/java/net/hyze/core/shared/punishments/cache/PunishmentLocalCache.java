package net.hyze.core.shared.punishments.cache;

import com.google.common.collect.Maps;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.LocalCache;
import net.hyze.core.shared.punishments.PunishmentCategory;
import net.hyze.core.shared.punishments.PunishmentRevokeCategory;
import net.hyze.core.shared.punishments.PunishmentType;
import java.util.Map;
import lombok.Getter;

public class PunishmentLocalCache implements LocalCache {

    @Getter
    private final Map<String, PunishmentType> types = Maps.newHashMap();

    @Getter
    private final Map<String, PunishmentCategory> categories = Maps.newHashMap();

    @Getter
    private final Map<String, PunishmentRevokeCategory> revokeCategories = Maps.newHashMap();

    public void put(PunishmentType type) {
        this.types.put(type.getName().toLowerCase(), type);
    }

    public void put(PunishmentCategory category) {
        this.categories.put(category.getName().toLowerCase(), category);
    }

    public void put(PunishmentRevokeCategory category) {
        this.revokeCategories.put(category.getName().toLowerCase(), category);
    }

    public PunishmentType getType(String key) {
        return this.types.get(key.toLowerCase());
    }

    public PunishmentCategory getCategory(String name) {
        return this.categories.get(name.toLowerCase());
    }

    public PunishmentRevokeCategory getRevokeCategory(String name) {
        return this.revokeCategories.get(name.toLowerCase());
    }

    public void clearCategories() {
        this.categories.clear();
    }

    public void clearUnbanCategories() {
        this.revokeCategories.clear();
    }

    @Override
    public void populate() {
        CoreProvider.Repositories.PUNISHMENTS.provide().fetchCategories().forEach(CoreProvider.Cache.Local.PUNISHMENTS.provide()::put);
        CoreProvider.Repositories.PUNISHMENTS.provide().fetchRevokeCategories().forEach(CoreProvider.Cache.Local.PUNISHMENTS.provide()::put);
    }

}
