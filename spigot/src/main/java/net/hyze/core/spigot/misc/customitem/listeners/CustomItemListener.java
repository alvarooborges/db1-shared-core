package net.hyze.core.spigot.misc.customitem.listeners;

import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.misc.customitem.CraftableCustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.StickyCustomItem;
import net.hyze.core.spigot.misc.customitem.events.PlayerUseCustomItemEvent;
import net.hyze.core.spigot.misc.enchantments.merchant.MerchantRecipeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.EventBus;

public class CustomItemListener implements Listener {

    private PlayerUseCustomItemEvent callPlayerUseCustomItemEvent(Player player, Event triggerEvent, CustomItem item) {
        PlayerUseCustomItemEvent event = new PlayerUseCustomItemEvent(player, triggerEvent, item);

        Bukkit.getServer().getPluginManager().callEvent(event);

        return event;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void on(CraftItemEvent event) {
        for (ItemStack stack : event.getInventory().getContents()) {
            
            CustomItem customItem = CustomItemRegistry.getByItemStack(stack);
            
            if (customItem != null && !(customItem instanceof CraftableCustomItem)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(BlockPlaceEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = event.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        CustomItem customItem;

        if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
            return;
        }

        if (callPlayerUseCustomItemEvent(player, event, customItem).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        EventBus bus = CustomItemRegistry.getEventBus(customItem);
        bus.post(event);
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        CustomItem customItem;

        if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
            return;
        }

        if (callPlayerUseCustomItemEvent(player, event, customItem).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        EventBus bus = CustomItemRegistry.getEventBus(customItem);
        bus.post(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerInteractEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        CustomItem customItem;

        if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
            return;
        }

        if (callPlayerUseCustomItemEvent(player, event, customItem).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        EventBus bus = CustomItemRegistry.getEventBus(customItem);
        bus.post(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(MerchantRecipeEvent event) {

        Player player = event.getPlayer();

        SECOND:
        {
            ItemStack item = event.getSecond();
            if (item == null || item.getType() == Material.AIR) {
                break SECOND;
            }

            CustomItem customItem;

            if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
                break SECOND;
            }

            if (callPlayerUseCustomItemEvent(player, event, customItem).isCancelled()) {
                event.setCancelled(true);
                return;
            }

            EventBus bus = CustomItemRegistry.getEventBus(customItem);
            bus.post(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(InventoryClickEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getWhoClicked();

        CURRENT_ITEM:
        {
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                break CURRENT_ITEM;
            }

            CustomItem customItem;

            if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
                break CURRENT_ITEM;
            }

            if (callPlayerUseCustomItemEvent(player, event, customItem).isCancelled()) {
                event.setCancelled(true);
                return;
            }

            EventBus bus = CustomItemRegistry.getEventBus(customItem);
            bus.post(event);
        }

        CURSOR_ITEM:
        {
            ItemStack item = event.getCursor();
            if (item == null || item.getType() == Material.AIR) {
                break CURSOR_ITEM;
            }

            CustomItem customItem;

            if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
                break CURSOR_ITEM;
            }

            if (callPlayerUseCustomItemEvent(player, event, customItem).isCancelled()) {
                event.setCancelled(true);
                return;
            }

            EventBus bus = CustomItemRegistry.getEventBus(customItem);
            bus.post(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerPickupItemEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getItem().getItemStack();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        CustomItem customItem;

        if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
            return;
        }

        if (callPlayerUseCustomItemEvent(event.getPlayer(), event, customItem).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        EventBus bus = CustomItemRegistry.getEventBus(customItem);
        bus.post(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerItemHeldEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        CustomItem customItem;

        if ((customItem = CustomItemRegistry.getByItemStack(item)) == null) {
            return;
        }

        if (callPlayerUseCustomItemEvent(event.getPlayer(), event, customItem).isCancelled()) {
            event.setCancelled(true);
            return;
        }

        EventBus bus = CustomItemRegistry.getEventBus(customItem);
        bus.post(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMonitor(PlayerDeathEvent event) {
        event.addKeepItemsFilter((slot, stack) -> CustomItemRegistry.getByItemStack(stack) instanceof StickyCustomItem);
        event.addKeepArmorFilter((slot, stack) -> CustomItemRegistry.getByItemStack(stack) instanceof StickyCustomItem);
    }
}
