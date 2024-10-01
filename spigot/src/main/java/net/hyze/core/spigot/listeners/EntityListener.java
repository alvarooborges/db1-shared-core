package net.hyze.core.spigot.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.user.preferences.UserPreference;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.commands.impl.basics.GodCommand;
import net.hyze.core.spigot.misc.customitem.data.LauncherItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.scheduler.BukkitTask;

public class EntityListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityCombustEvent event) {
        if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onLow(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager.hasMetadata(CoreSpigotConstants.NBTKeys.ENTITY_TRUE_DAMAGE)) {
            double damage = damager.getMetadata(CoreSpigotConstants.NBTKeys.ENTITY_TRUE_DAMAGE).get(0).asDouble();

            event.setDamage(EntityDamageEvent.DamageModifier.BASE, damage);

            event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHighest(EntityDamageByEntityEvent event) {
        Player player = null;
        Player damager = null;

        if (event.getEntity() instanceof Player) {
            player = (Player) event.getEntity();
        }

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
                damager = (Player) ((Projectile) event.getDamager()).getShooter();
            }
        }

        event.setCancelled(GodCommand.anyGod(player, damager));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMonitor(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        if (!entity.hasMetadata("showHealthOnName")) {
            return;
        }

        double health = entity.getHealth() - event.getFinalDamage();

        if (health < 1.0) {
            entity.setCustomName((String) null);
            entity.setCustomNameVisible(false);
        } else {
            entity.setCustomName(Integer.toString((int) health) + ChatColor.RED + " \u2764");
            entity.setCustomNameVisible(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        if (!entity.hasMetadata("showHealthOnName")) {
            return;
        }

        double health = entity.getHealth() + event.getAmount();

        if (health < 1.0) {
            entity.setCustomName((String) null);
            entity.setCustomNameVisible(false);
        } else {
            entity.setCustomName(Integer.toString((int) health) + ChatColor.RED + " \u2764");
            entity.setCustomNameVisible(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();

        if (!entity.hasMetadata("showHealthOnName")) {
            return;
        }

        if (entity.getHealth() < 1.0) {
            entity.setCustomName((String) null);
            entity.setCustomNameVisible(false);
        } else {
            entity.setCustomName(Integer.toString((int) entity.getHealth()) + ChatColor.RED + " \u2764");
            entity.setCustomNameVisible(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMonitor(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        Location deathLocation = player.getLocation();

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && player.hasMetadata(CoreSpigotConstants.NBTKeys.PLAYER_FALL_DAMAGE_BYPASS)) {
            event.setCancelled(true);
            player.removeMetadata(CoreSpigotConstants.NBTKeys.PLAYER_FALL_DAMAGE_BYPASS, CoreSpigotPlugin.getInstance());
            return;
        }

//        Printer.INFO.print(player.getName(), event.getFinalDamage(), player.getHealth());
//        if (event.getFinalDamage() >= player.getHealth()) {
//            event.setDamage(0);
//            player.setHealth(0.5);
//
//            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());
//
//            List<ItemStack> loot = Lists.newArrayList();
//
//            Collections.addAll(loot, player.getInventory().getArmorContents());
//            Collections.addAll(loot, player.getInventory().getContents());
//
//            CraftPlayer craftPlayer = (CraftPlayer) player;
//            EntityPlayer entityPlayer = craftPlayer.getHandle();
//
//            UserDeathEvent deathEvent = new UserDeathEvent(user, player, loot, entityPlayer.getExpReward());
//
//            Bukkit.getServer().getPluginManager().callEvent(deathEvent);
//
//            entityPlayer.keepLevel = deathEvent.getKeepLevel();
//            entityPlayer.newLevel = deathEvent.getNewLevel();
//            entityPlayer.newTotalExp = deathEvent.getNewTotalExp();
//            entityPlayer.expToDrop = deathEvent.getDroppedExp();
//            entityPlayer.newExp = deathEvent.getNewExp();
//            entityPlayer.fireTicks = 0;
//
//            if (!deathEvent.getKeepInventory()) {
//                for (PotionEffect effect : player.getActivePotionEffects()) {
//                    player.removePotionEffect(effect.getType());
//                }
//
//                for (ItemStack stack : loot) {
//                    if (stack == null || stack.getType() == Material.AIR) {
//                        continue;
//                    }
//
//                    player.getWorld().dropItemNaturally(deathLocation, stack);
//                }
//
//                player.closeInventory();
//
//                player.getInventory().clear();
//                player.getInventory().setArmorContents(null);
//            }
//        }
    }
}
