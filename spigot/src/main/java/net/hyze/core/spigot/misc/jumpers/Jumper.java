package net.hyze.core.spigot.misc.jumpers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.hyze.core.spigot.CoreSpigotPlugin;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

public class Jumper {

    public static final String IDENTIFIER = "JUMPER_LOCATION";
    public static final String IDENTIFIER_MULTIPLY = "JUMPER_MULTIPLY";
    public static final String IDENTIFIER_Y = "JUMPER_Y";

    private final Multimap<Location, Set<Location>> jumpers = HashMultimap.create();

    public Jumper jumper(Location locationTo, Set<Location> blockLocations) {
        return jumper(locationTo, blockLocations, 4, 1);
    }

    public Jumper jumper(Location locationTo, Set<Location> blockLocations, int multiply, int y) {

        blockLocations.forEach(target -> {

            target.getChunk().load();

            Block block = target.getBlock();

            block.setMetadata(
                    IDENTIFIER,
                    new FixedMetadataValue(CoreSpigotPlugin.getInstance(), locationTo)
            );

            block.setMetadata(
                    IDENTIFIER_MULTIPLY,
                    new FixedMetadataValue(CoreSpigotPlugin.getInstance(), multiply)
            );

            block.setMetadata(
                    IDENTIFIER_Y,
                    new FixedMetadataValue(CoreSpigotPlugin.getInstance(), y)
            );

        });

        this.jumpers.put(locationTo, blockLocations);
        return this;
    }

    public void start() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CoreSpigotPlugin.getInstance(), () -> {

            this.jumpers.values().forEach(blocks -> {
                blocks.forEach(raw -> {
                    Location location = raw.clone().add(0.5, 1, 0.5);
                    location.getWorld()
                            .spigot()
                            .playEffect(location, Effect.HAPPY_VILLAGER, 1, 0, 0.5F, 0.1F, 0.5F, 1, 5, 15);
                });
            });

        }, 0L, 10L);
    }

}
