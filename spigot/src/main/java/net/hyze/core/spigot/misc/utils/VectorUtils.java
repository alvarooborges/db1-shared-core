package net.hyze.core.spigot.misc.utils;

import net.hyze.core.shared.misc.utils.Vector2D;
import org.bukkit.util.Vector;

public class VectorUtils {

    public static Vector rotateY(Vector vector, double theta) {
        return new Vector(vector.getX() * Math.cos(theta) - vector.getZ() * Math.sin(theta),
            vector.getY(),
            vector.getX() * Math.sin(theta) + vector.getZ() * Math.cos(theta));
    }

    public static Vector2D rotate(Vector2D vector, double theta) {
        return new Vector2D(vector.getX() * Math.cos(theta) - vector.getZ() * Math.sin(theta),
            vector.getX() * Math.sin(theta) + vector.getZ() * Math.cos(theta));
    }

}
