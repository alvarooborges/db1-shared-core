package net.hyze.core.spigot.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class WorldListener implements Listener {

    // TODO Skyblock fix

    @EventHandler
    public void on(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof ArmorStand) {
                entity.remove();
            }
        }
    }
}
