package net.hyze.core.spigot.misc.stackmobs;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.stackmobs.listeners.ChunkLoadListener;
import net.hyze.core.spigot.misc.stackmobs.listeners.CreatureSpawnListener;
import net.hyze.core.spigot.misc.stackmobs.listeners.EntityDeathListener;
import net.hyze.core.spigot.misc.stackmobs.tasks.UpdateDisplayNameTask;
import net.hyze.core.spigot.misc.utils.NMS;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.NonNull;
import net.hyze.core.spigot.misc.stackmobs.tasks.MergetTask;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class StackMobsAPI {

    public static final String STACK_SIZE_TAG = "stackmobs:stack-size";
    public static final String NEW_STACK_ENTITY_TAG = "stackmobs:stack-size";
    public static final String PREVENT_STACK_TAG = "stackmobs:prevent-stack";
    public static final String SINGLE_KILL_TAG = "stackmobs:single-kill";

    public static Vector CHECK_AREA = new Vector(7, 4, 7);

    public static final Set<CreatureSpawnEvent.SpawnReason> NO_STACK_REASONS = Sets.newConcurrentHashSet();
    public static final Set<EntityType> NO_STACK_TYPES = Sets.newConcurrentHashSet();

    public static Multimap<String, World> WORLDS = HashMultimap.create();

    private static boolean ENABLED = false;

    public static void disable() {
        ENABLED = false;
    }

    public static void enable() {
        if (ENABLED) {
            return;
        }

        ENABLED = true;

        PluginManager pluginManager = CoreSpigotPlugin.getInstance().getServer().getPluginManager();
        pluginManager.registerEvents(new CreatureSpawnListener(), CoreSpigotPlugin.getInstance());
        pluginManager.registerEvents(new EntityDeathListener(), CoreSpigotPlugin.getInstance());
        pluginManager.registerEvents(new ChunkLoadListener(), CoreSpigotPlugin.getInstance());

        new UpdateDisplayNameTask().runTaskTimer(CoreSpigotPlugin.getInstance(), 0, 10);
        new MergetTask().runTaskTimer(CoreSpigotPlugin.getInstance(), 0, 20 * 3);
    }

    public static StackedEntity getStackedEntity(Entity entity) {
        return new StackedEntity(entity);
    }

    public static boolean handle(Entity entity, CreatureSpawnEvent.SpawnReason reason, Location location) {
        StackedEntity stacked = new StackedEntity(entity);

        if (stacked.isStackingPrevented()) {
            return false;
        }

        if (stacked.getSize() >= 300) {
            return true;
        }

        return handle(entity.getType(), reason, location);
    }

    public static void updateEntityName(Entity entity) {
        StackedEntity stacked = new StackedEntity(entity);

        if (!stacked.isStackingPrevented() && stacked.hasStackSizeTag()) {
            entity.setCustomName(StackMobsAPI.getMobName(entity.getType(), stacked.getSize()));

            entity.setCustomNameVisible(true);
        }
    }

    public static boolean handle(EntityType type, CreatureSpawnEvent.SpawnReason reason, Location location) {
        if (type == EntityType.ARMOR_STAND) {
            return false;
        }

        if (NO_STACK_REASONS.contains(reason)) {
            return false;
        }

        if (NO_STACK_TYPES.contains(type)) {
            return false;
        }

        double xLoc = CHECK_AREA.getX();
        double yLoc = CHECK_AREA.getY();
        double zLoc = CHECK_AREA.getZ();

        for (Entity nearby : location.getWorld().getNearbyEntities(location, xLoc, yLoc, zLoc)) {
            if (type != nearby.getType() || nearby.isDead()) {
                continue;
            }

            StackedEntity stacked = new StackedEntity(nearby);

            if (stacked.isStackingPrevented()) {
                continue;
            }

            if (stacked.getSize() >= 300) {
                return true;
            }

            stacked.setSize(stacked.getSize() + 1);
            return true;
        }

        return false;
    }

    public static Entity duplicate(@NonNull Entity original, Consumer<net.minecraft.server.v1_8_R3.Entity> preSpawn) {
        net.minecraft.server.v1_8_R3.Entity entity;

        CraftWorld world = (CraftWorld) original.getWorld();

        if (original instanceof Zombie || original instanceof Skeleton) {
            Location location = new Location(
                    original.getWorld(),
                    original.getLocation().getBlockX() + 0.5,
                    original.getLocation().getY(),
                    original.getLocation().getBlockZ() + 0.5
            );

            entity = world.createEntity(location, original.getType().getEntityClass());
        } else {
            entity = world.createEntity(original.getLocation(), original.getType().getEntityClass());
        }

        preSpawn.accept(entity);

        Entity clone = world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);

        setAi(clone);

        return cloneTraits(original, clone);
    }

    public static Entity cloneTraits(Entity original, Entity clone) {
        return clone;
    }

    public static void setAi(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();

        if (nmsEntity instanceof EntityInsentient) {
            NMS.clearEntitySelectors((EntityInsentient) nmsEntity);
        }
    }

    public static Set<Entity> getLoadedEntities() {
        Set<Entity> entities = Sets.newHashSet();

        Bukkit.getOnlinePlayers().stream()
                .filter(Player::isOnline)
                .forEach((player) -> {
                    entities.addAll(player.getNearbyEntities(20, 20, 20));
                });

        return entities.stream()
                .filter(entity -> WORLDS.containsEntry(CoreProvider.getApp().getId(), entity.getWorld()))
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> entity.hasMetadata(STACK_SIZE_TAG))
                .collect(Collectors.toSet());
    }

    public static String getMobName(EntityType type, int size) {
        String[] names = type.name().replace("_", " ").toLowerCase().split(" ");
        String name = "";

        for (String name1 : names) {
            name = name1.substring(0, 1).toUpperCase() + name1.substring(1).toLowerCase() + " ";
        }

        if (name.equals("")) {
            name = type.name().replace("_", " ").substring(0, 1).toUpperCase() + type.name().replace("_", " ").substring(1).toLowerCase();
        }

        return ChatColor.YELLOW.toString() + size + "x " + name.trim();
    }
}
