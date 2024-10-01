package net.hyze.core.spigot.misc.mining;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Getter
public enum MiningTool implements BaseTool {

    OTHER(Material.AIR),
    WOOD(Material.WOOD_PICKAXE),
    GOLD(Material.GOLD_PICKAXE),
    STONE(Material.STONE_PICKAXE),
    IRON(Material.IRON_PICKAXE),
    DIAMOND(Material.DIAMOND_PICKAXE);

    private final Material material;

    @Override
    public boolean isValidTool(Material tool) {
        return getMiningTool(tool).ordinal() >= ordinal();
    }

    public static MiningTool getMiningTool(Material tool) {
        for (MiningTool mt : values()) {
            if (mt.getMaterial().equals(tool)) {
                return mt;
            }
        }
        return OTHER;
    }

    public static boolean isTool(Material tool) {
        if (tool.equals(Material.AIR)) {
            return false;
        }
        for (MiningTool mt : values()) {
            if (mt.getMaterial().equals(tool)) {
                return true;
            }
        }
        return false;
    }

}
