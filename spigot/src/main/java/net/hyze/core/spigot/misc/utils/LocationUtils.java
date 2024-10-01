package net.hyze.core.spigot.misc.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorldBorder;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public class LocationUtils {

    public static final HashSet<Material> BLOCKS_NONFULL_LIST = Sets.newHashSet(
            Material.WALL_SIGN,
            Material.SIGN,
            Material.SIGN_POST,
            Material.WOOD_BUTTON,
            Material.STONE_BUTTON,
            Material.STONE_PLATE,
            Material.WOOD_PLATE,
            Material.FENCE,
            Material.TRAP_DOOR,
            Material.IRON_FENCE,
            Material.THIN_GLASS,
            Material.FENCE_GATE,
            Material.BRICK_STAIRS,
            Material.SMOOTH_STAIRS,
            Material.NETHER_FENCE,
            Material.NETHER_BRICK_STAIRS,
            Material.ENCHANTMENT_TABLE,
            Material.ENDER_PORTAL_FRAME,
            Material.WOOD_STEP,
            Material.SANDSTONE_STAIRS,
            Material.ENDER_CHEST,
            Material.SPRUCE_WOOD_STAIRS,
            Material.BIRCH_WOOD_STAIRS,
            Material.JUNGLE_WOOD_STAIRS,
            Material.COBBLE_WALL,
            Material.TRAPPED_CHEST,
            Material.GOLD_PLATE,
            Material.IRON_PLATE,
            Material.DAYLIGHT_DETECTOR,
            Material.HOPPER,
            Material.QUARTZ_STAIRS,
            Material.STAINED_GLASS_PANE,
            Material.ACACIA_STAIRS,
            Material.DARK_OAK_STAIRS,
            Material.IRON_TRAPDOOR,
            Material.RED_SANDSTONE_STAIRS,
            Material.STONE_SLAB2,
            Material.SPRUCE_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.ACACIA_FENCE_GATE,
            Material.SPRUCE_FENCE,
            Material.BIRCH_FENCE,
            Material.JUNGLE_FENCE,
            Material.DARK_OAK_FENCE,
            Material.ACACIA_FENCE,
            Material.WOOD_DOOR,
            Material.BED,
            Material.CAULDRON_ITEM,
            Material.BANNER,
            Material.SPRUCE_DOOR_ITEM,
            Material.BIRCH_DOOR_ITEM,
            Material.JUNGLE_DOOR_ITEM,
            Material.ACACIA_DOOR_ITEM,
            Material.DARK_OAK_DOOR_ITEM,
            Material.STEP,
            Material.SOUL_SAND
    );

    public static boolean isNearLocation(Location from, Location to, double discance) {

        if (from == null || to == null) {
            return false;
        }
        if (!from.getWorld().getName().equalsIgnoreCase(to.getWorld().getName())) {
            return false;
        }
        if (!(from.getX() <= to.getX() + discance && from.getX() >= to.getX() - discance)) {
            return false;
        }
        if (!(from.getY() <= to.getY() + discance && from.getY() >= to.getY() - discance)) {
            return false;
        }
        return from.getZ() <= to.getZ() + discance && from.getZ() >= to.getZ() - discance;
    }

    public static boolean compareLocation(Location first, Location second) {
        return first.getWorld().getName().equalsIgnoreCase(second.getWorld().getName())
                && first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
    }

    public static boolean isInBoundsOfBorder(Chunk chunk) {
        return ((CraftWorld) chunk.getWorld()).getHandle().getWorldBorder().isInBounds(chunk.getX(), chunk.getZ());
    }

    public static boolean isOutsideOfBorder(Location location) {
        CraftWorld craftWorld = (CraftWorld) location.getWorld();

        return !craftWorld.getHandle().getWorldBorder().a(new BlockPosition(location.getX(), location.getY(), location.getZ()));
    }

    public static boolean isSameChunk(Location loc1, Location loc2) {
        return loc1.getChunk().getX() == loc2.getChunk().getX()
                && loc1.getChunk().getZ() == loc2.getChunk().getZ();
    }

    public static Vector calculateVector(Location from, Location to) {
        return to.toVector().subtract(from.toVector());
    }

    public static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
    }

    public static Location lookAt(Location loc, Location lookat) {
        return loc.clone().setDirection(calculateVector(loc, lookat));
    }

    public static Location center(Location loc) {
        return new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 0.5, loc.getBlockZ() + 0.5, loc.getYaw(), loc.getPitch());
    }

    public static List<Location> getChunkBoundingBoxLocations(Location location, int steps, int step) {

        List<Location> ret = Lists.newArrayList();

        final World world = location.getWorld();

        final int xmin = location.getChunk().getX() * 16;
        final int xmax = xmin + 15;
        final double y = location.getBlockY() + 2;
        final int zmin = location.getChunk().getZ() * 16;
        final int zmax = zmin + 15;

        int keepEvery = 5;
        if (keepEvery <= 0) {
            keepEvery = Integer.MAX_VALUE;
        }

        int skipEvery = 0;
        if (skipEvery <= 0) {
            skipEvery = Integer.MAX_VALUE;
        }

        int x = xmin;
        int z = zmin;
        int i = 0;

        while (x + 1 <= xmax) {
            x++;
            i++;
            if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) {
                ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
            }
        }

        while (z + 1 <= zmax) {
            z++;
            i++;
            if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) {
                ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
            }
        }

        while (x - 1 >= xmin) {
            x--;
            i++;
            if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) {
                ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
            }
        }

        while (z - 1 >= zmin) {
            z--;
            i++;
            if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) {
                ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
            }
        }

        return ret;
    }
}
