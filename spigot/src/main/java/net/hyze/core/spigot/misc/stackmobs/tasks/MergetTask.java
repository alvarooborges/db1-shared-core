package net.hyze.core.spigot.misc.stackmobs.tasks;

import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.stackmobs.StackedEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class MergetTask extends BukkitRunnable {

    @Override
    public void run() {
        StackMobsAPI.getLoadedEntities().forEach(entity -> {
            if (!entity.isDead()) {
                StackedEntity stacked = new StackedEntity(entity);

                if (stacked.getSize() >= 300) {
                    return;
                }

                if (!stacked.isStackingPrevented() && stacked.hasStackSizeTag()) {

                    double xLoc = StackMobsAPI.CHECK_AREA.getX();
                    double yLoc = StackMobsAPI.CHECK_AREA.getY();
                    double zLoc = StackMobsAPI.CHECK_AREA.getZ();

                    Location loc = entity.getLocation();

                    EntityType type = entity.getType();

                    for (Entity nearby : loc.getWorld().getNearbyEntities(loc, xLoc, yLoc, zLoc)) {
                        if (type != nearby.getType() || nearby.isDead()) {
                            continue;
                        }
                        
                        if (nearby.getEntityId() == entity.getEntityId()) {
                            continue;
                        }

                        StackedEntity nearbyStacked = new StackedEntity(nearby);

                        if (nearbyStacked.isStackingPrevented()) {
                            continue;
                        }

                        if (stacked.getSize() + nearbyStacked.getSize() >= 300) {
                            continue;
                        }

                        stacked.setSize(stacked.getSize() + nearbyStacked.getSize());
                        nearby.remove();
                    }
                }
            }
        });
    }
}
