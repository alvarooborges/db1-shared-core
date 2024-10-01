package net.hyze.core.spigot.listeners;

import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.CustomPlayerInventory;
import net.minecraft.server.v1_8_R3.IInventory;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener {

    @EventHandler
    public void on(InventoryDragEvent event) {
        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onDrag(event);
        }

        if (iInventory instanceof CustomPlayerInventory) {
            //SpigotUser spigotUser = API.getInstance().getUserFactory().getLeader(player);
            ((CustomPlayerInventory) iInventory).onDrag(event);
        }
    }

    @EventHandler
    public void on(InventoryClickEvent event) {

        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getClick() == ClickType.DOUBLE_CLICK || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onClick(event);
        }

        if (iInventory instanceof CustomPlayerInventory) {
            //SpigotUser spigotUser = API.getInstance().getUserFactory().getLeader(player);
            //((CustomPlayerInventory) iInventory).onClick(spigotUser.getGroups().getHighestGroup(), event);
            ((CustomPlayerInventory) iInventory).onClick(event);
        }

    }

    @EventHandler
    public void on(InventoryOpenEvent event) {

        if (CoreSpigotConstants.STOPPING) {
            event.setCancelled(true);
            return;
        }

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;

        IInventory inventory_ = craftInventory.getInventory();
        if (inventory_ instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) inventory_).getParent().onOpen(event);
        }

    }

    @EventHandler
    public void on(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onClose(event);
        }

    }

    @EventHandler
    public void on(InventoryRegroupItemEvent event) {
        Inventory inventory = event.getInventory();
        CraftInventory craftInventory = (CraftInventory) inventory;
        IInventory iInventory = craftInventory.getInventory();

        if (iInventory instanceof CustomInventory.MinecraftInventory) {
            ((CustomInventory.MinecraftInventory) iInventory).getParent().onRegroupItem(event);
        }
    }
}
