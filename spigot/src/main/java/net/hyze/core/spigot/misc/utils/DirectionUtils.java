package net.hyze.core.spigot.misc.utils;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class DirectionUtils {

    public static final BlockFace[] AXIAL = {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };

    public static final BlockFace[] RADIAL = {
        BlockFace.NORTH,
        BlockFace.NORTH_EAST,
        BlockFace.EAST,
        BlockFace.SOUTH_EAST,
        BlockFace.SOUTH,
        BlockFace.SOUTH_WEST,
        BlockFace.WEST,
        BlockFace.NORTH_WEST
    };

    public static BlockFace yawToFace(float yaw) {
        BlockFace face = yawToFace(yaw, true);

        switch (face) {
            default:
                return null;
            case NORTH:
                return BlockFace.SOUTH;
            case SOUTH:
                return BlockFace.NORTH;
            case EAST:
                return BlockFace.WEST;
            case WEST:
                return BlockFace.EAST;
            case NORTH_WEST:
                return BlockFace.SOUTH_WEST;
            case NORTH_EAST:
                return BlockFace.SOUTH_EAST;
            case SOUTH_WEST:
                return BlockFace.NORTH_EAST;
            case SOUTH_EAST:
                return BlockFace.NORTH_WEST;
        }
    }

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return RADIAL[Math.round(yaw / 45f) & 0x7];
        } else {
            return AXIAL[Math.round(yaw / 90f) & 0x3];
        }
    }

    public static BlockFace vectorToFace(Vector vector) {
        double absX = Math.abs(vector.getX()),
                absZ = Math.abs(vector.getZ()),
                x = vector.getX(),
                z = vector.getZ();
        return absX > absZ ? (x > 0 ? BlockFace.EAST : BlockFace.WEST) : (z > 0 ? BlockFace.SOUTH : BlockFace.NORTH);
    }
}
