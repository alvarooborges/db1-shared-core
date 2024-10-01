package net.hyze.core.spigot.misc.stackmobs.tasks;

import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateDisplayNameTask extends BukkitRunnable {

    @Override
    public void run() {
        StackMobsAPI.getLoadedEntities().forEach(entity -> {
            StackMobsAPI.updateEntityName(entity);
        });
    }

}
