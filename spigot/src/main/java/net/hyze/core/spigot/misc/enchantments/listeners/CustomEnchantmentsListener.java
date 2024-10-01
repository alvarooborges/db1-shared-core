package net.hyze.core.spigot.misc.enchantments.listeners;

import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.events.ArmorEquipEvent;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.enchantments.triggers.ArmorEquipTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageByEntityTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityDamageTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.EntityShootBowTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerInteractTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerItemDamageTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.PlayerMoveTrigger;
import net.hyze.core.spigot.misc.enchantments.triggers.TriggerFactory;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import java.util.Map;
import net.hyze.core.spigot.misc.enchantments.triggers.BlockBreakTrigger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class CustomEnchantmentsListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(EntityDamageByEntityEvent event) {
        if (!event.getClass().equals(EntityDamageByEntityEvent.class)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            TriggerFactory.post((Player) event.getEntity(), event, EntityDamageByEntityTrigger.class);
        }

        if (event.getDamager() instanceof Player) {
            TriggerFactory.post((Player) event.getDamager(), event, EntityDamageByEntityTrigger.class);
        }

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }

            if (!projectile.hasMetadata("bow_custom_enchantments") || !projectile.hasMetadata("bow_custom_enchanted_item")) {
                return;
            }

            Map<CustomEnchantment, Integer> map = (Map<CustomEnchantment, Integer>) projectile.getMetadata("bow_custom_enchantments").get(0).value();

            for (CustomEnchantment enchantment : map.keySet()) {
                TriggerFactory.post((Player) projectile.getShooter(),
                        event,
                        (ItemStack) projectile.getMetadata("bow_custom_enchanted_item").get(0).value(),
                        enchantment,
                        EntityDamageByEntityTrigger.class
                );
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(EntityDamageEvent event) {
        if (!event.getClass().equals(EntityDamageEvent.class)) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            TriggerFactory.post((Player) event.getEntity(), event, EntityDamageTrigger.class);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Map<CustomEnchantment, Integer> map = CustomEnchantmentUtil.getEnchantments(event.getBow());

        if (map.isEmpty()) {
            return;
        }

        event.getProjectile().setMetadata("bow_custom_enchanted_item", new FixedMetadataValue(CoreSpigotPlugin.getInstance(), event.getBow()));
        event.getProjectile().setMetadata("bow_custom_enchantments", new FixedMetadataValue(CoreSpigotPlugin.getInstance(), map));

        TriggerFactory.post((Player) event.getEntity(), event, event.getBow(), EntityShootBowTrigger.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent event) {
        ItemStack stack = event.getPlayer().getItemInHand();

        if (stack != null && stack.getType() != Material.AIR) {
            TriggerFactory.post(event.getPlayer(), event, stack, PlayerInteractTrigger.class);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerItemDamageEvent event) {
        TriggerFactory.post(event.getPlayer(), event, event.getItem(), PlayerItemDamageTrigger.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(PlayerMoveEvent event) {
        if (LocationUtils.compareLocation(event.getFrom(), event.getTo())) {
            return;
        }

        TriggerFactory.post(event.getPlayer(), event, PlayerMoveTrigger.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(BlockBreakEvent event) {
        if (!event.getClass().equals(BlockBreakEvent.class)) {
            return;
        }

        TriggerFactory.post(event.getPlayer(), event, BlockBreakTrigger.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(ArmorEquipEvent event) {
        if (event.getNewArmorPiece() != null && event.getNewArmorPiece().getType() != Material.AIR) {
            TriggerFactory.post(event.getPlayer(), event, event.getNewArmorPiece(), ArmorEquipTrigger.class);
        }

        if (event.getOldArmorPiece() != null && event.getOldArmorPiece().getType() != Material.AIR) {
            TriggerFactory.post(event.getPlayer(), event, event.getOldArmorPiece(), ArmorEquipTrigger.class);
        }
    }
}
