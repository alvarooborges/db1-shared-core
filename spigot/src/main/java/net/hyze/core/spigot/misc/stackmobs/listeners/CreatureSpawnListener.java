package net.hyze.core.spigot.misc.stackmobs.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.stackmobs.StackedEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawnListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(CreatureSpawnEvent event) {
        if (!StackMobsAPI.WORLDS.containsEntry(CoreProvider.getApp().getId(), event.getLocation().getWorld())) {
            return;
        }

        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();
        CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();

        if (type == EntityType.ARMOR_STAND) {
            return;
        }

        if (StackMobsAPI.NO_STACK_REASONS.contains(reason)) {
            return;
        }

        if (StackMobsAPI.NO_STACK_TYPES.contains(type)) {
            return;
        }

        if (entity.hasMetadata(StackMobsAPI.NEW_STACK_ENTITY_TAG)) {
            entity.removeMetadata(StackMobsAPI.NEW_STACK_ENTITY_TAG, CoreSpigotPlugin.getInstance());
            return;
        }

        if (StackMobsAPI.handle(entity, reason, event.getLocation())) {
            entity.remove();
            return;
        }

        StackedEntity stacked = new StackedEntity(entity);
        stacked.setSize(1);

        StackMobsAPI.setAi(entity);
    }
}
