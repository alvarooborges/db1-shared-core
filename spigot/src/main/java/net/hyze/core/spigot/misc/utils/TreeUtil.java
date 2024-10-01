package net.hyze.core.spigot.misc.utils;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class TreeUtil {

    private static final List<Material> MATERIALS = Lists.newArrayList(
            Material.LEAVES,
            Material.LEAVES_2,
            Material.LOG,
            Material.LOG_2,
            Material.VINE
    );

    private static final List<BlockFace> FACES = Lists.newArrayList(
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.EAST,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.WEST
    );

    public static void removeTree(Block block) {
        removeTree(block, Lists.newArrayList());
    }

    public static void removeTree(Block block, List<Location> locations) {
        if (block == null || !MATERIALS.contains(block.getType())) {
            return;
        }

        FACES.forEach(face -> {
            Block relative = block.getRelative(face);

            if (MATERIALS.contains(relative.getType()) && !locations.contains(relative.getLocation())) {

                locations.add(relative.getLocation());
                removeTree(relative, locations);

            }
        });

        locations.forEach(location -> location.getBlock().setType(Material.AIR));
    }
}
