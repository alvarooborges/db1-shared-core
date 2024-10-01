package net.hyze.core.spigot.world.location.unserializer;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.world.location.SerializedLocation;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import net.hyze.core.shared.world.location.LocationParser;

@NoArgsConstructor
public class BukkitLocationParser implements LocationParser<Location> {

    @Override
    public Location apply(SerializedLocation serialized) {
        return new Location(
                Bukkit.getWorld(serialized.getWorldName()),
                serialized.getX(),
                serialized.getY(),
                serialized.getZ(),
                serialized.getYaw(),
                serialized.getPitch()
        );
    }

    public static SerializedLocation serialize(Location location) {
        return serialize(CoreProvider.getApp().getId(), location);
    }

    public static SerializedLocation serialize(String appId, Location location) {
        return new SerializedLocation(
                appId,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );
    }
}
