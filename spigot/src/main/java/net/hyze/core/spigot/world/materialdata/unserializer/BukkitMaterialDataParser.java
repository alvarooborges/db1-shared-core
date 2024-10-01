package net.hyze.core.spigot.world.materialdata.unserializer;

import net.hyze.core.shared.world.materialdata.MaterialDataParser;
import net.hyze.core.shared.world.materialdata.SerializedMaterialData;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class BukkitMaterialDataParser implements MaterialDataParser<MaterialData> {

    private static BukkitMaterialDataParser instance;

    @Override
    public MaterialData apply(SerializedMaterialData serialized) {
        Material type = Material.getMaterial(serialized.getMaterialRaw());

        if (type == null) {
            return null;
        }

        return new MaterialData(type, serialized.getData());
    }

    public static SerializedMaterialData serialize(MaterialData data) {
        return new SerializedMaterialData(
                data.getItemType().name(),
                data.getData()
        );
    }

    public static BukkitMaterialDataParser getInstance() {
        if (instance == null) {
            instance = new BukkitMaterialDataParser();
        }

        return instance;
    }

}
