package net.hyze.core.spigot.misc.stackmobs.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.stackmobs.StackedEntity;
import net.hyze.core.spigot.misc.stackmobs.events.StackMobDeathEvent;
import net.hyze.core.spigot.misc.utils.NMS;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        if (!StackMobsAPI.WORLDS.containsEntry(CoreProvider.getApp().getId(), event.getEntity().getWorld())) {
            return;
        }

        LivingEntity entity = event.getEntity();
        EntityType type = entity.getType();

        if (StackMobsAPI.NO_STACK_TYPES.contains(type)) {
            return;
        }

        StackedEntity stacked = new StackedEntity(entity);

        StackMobDeathEvent stackMobDeathEvent = new StackMobDeathEvent(stacked, 1, event);

        Bukkit.getPluginManager().callEvent(stackMobDeathEvent);
        
        int withdraw = stackMobDeathEvent.getDeathAmount();

        if (withdraw >= stacked.getSize()) {
            return;
        }

        Entity newEntity = spawnNewEntity(stacked.getSize(), withdraw, entity);

        if (newEntity != null) {
            StackMobsAPI.updateEntityName(newEntity);
        }
    }

    public static Entity spawnNewEntity(int oldSize, int withdraw, Entity dead) {

        dead.removeMetadata(StackMobsAPI.STACK_SIZE_TAG, CoreSpigotPlugin.getInstance());

        Entity clone = StackMobsAPI.duplicate(dead, entity -> {
            entity.fromMobSpawner = true;
            entity.getBukkitEntity().setMetadata(StackMobsAPI.NEW_STACK_ENTITY_TAG, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));
        });

        if (clone != null) {
            StackedEntity stacked = new StackedEntity(clone);
            stacked.setSize(oldSize - withdraw);

            ((LivingEntity) clone).setCanPickupItems(false);

            if (clone instanceof Zombie) {
                ((Zombie) clone).setBaby(false);
                ((Zombie) clone).setVillager(false);
            }

            if (clone instanceof PigZombie) {
                ((PigZombie) clone).setBaby(false);
            }

            if (clone instanceof Slime) {
                ((Slime) clone).setSize(3);
            }

            if (clone instanceof MagmaCube) {
                ((MagmaCube) clone).setSize(3);
            }

            if (clone instanceof Skeleton) {
                ((Skeleton) clone).setSkeletonType(Skeleton.SkeletonType.NORMAL);
            }

            return clone;
        }

        return null;
    }
}
