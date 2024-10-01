package net.hyze.core.spigot.misc.utils;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EntityUtils {

    public static List<Entity> getNearbyEntities(Location loc, int radius) {
        List<Entity> out = new ArrayList<>();

        World world = loc.getWorld();

        for (Entity e : world.getEntities()) {
            if (e.getLocation().distanceSquared(loc) <= radius * radius) {
                out.add(e);
            }
        }

        return out;
    }

    public static boolean isStucked(LivingEntity entity) {
        for (int y = entity.getLocation().getBlockY(); y <= entity.getEyeLocation().getBlockY(); y++) {
            Location location = new Location(entity.getWorld(), entity.getLocation().getBlockX(), y, entity.getLocation().getBlockZ());
            Material type = location.getBlock().getType();
            if (type.isSolid() && !LocationUtils.BLOCKS_NONFULL_LIST.contains(type)) {
                return true;
            }
        }
        return false;
    }

    public static int getPlayerTotalExp(Player player) {
        int level = player.getLevel();
        long totalInLvl = level <= 16 ? Math.round(Math.pow(level, 2) + (6 * level))
                : (level <= 31
                        ? Math.round((2.5 * Math.pow(level, 2)) - (40.5 * level) + 360)
                        : Math.round((4.5 * Math.pow(level, 2)) - (162.5 * level) + 2220));
        return (int) totalInLvl + Math.round(player.getExpToLevel() * player.getExp());
    }

}
