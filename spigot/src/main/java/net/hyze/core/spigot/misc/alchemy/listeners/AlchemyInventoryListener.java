package net.hyze.core.spigot.misc.alchemy.listeners;

import net.hyze.core.spigot.misc.alchemy.AlchemyManager;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class AlchemyInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClickNormal(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        BrewingStand stand = (BrewingStand) holder;
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        System.out.println("clicked" + clicked);
        System.out.println("cursor" + cursor);

        if ((clicked != null && clicked.getType() == Material.POTION)
                || (cursor != null && cursor.getType() == Material.POTION)) {
            AlchemyManager.scheduleCheck(player, stand);
            return;
        }

        ClickType click = event.getClick();
        InventoryType.SlotType slot = event.getSlotType();

        if (click.isShiftClick()) {
            switch (slot) {
                case FUEL:
                    AlchemyManager.scheduleCheck(player, stand);
                    return;
                case CONTAINER:
                case QUICKBAR:
                    if (!AlchemyManager.isValidIngredient(player, clicked)) {
                        return;
                    }

                    if (!AlchemyManager.transferItems(event.getView(), event.getRawSlot(), click)) {
                        return;
                    }

                    event.setCancelled(true);
                    AlchemyManager.scheduleUpdate(inventory);
                    AlchemyManager.scheduleCheck(player, stand);
                    return;
                default:
                    return;
            }
        } else if (slot == InventoryType.SlotType.FUEL) {
            boolean emptyClicked = AlchemyManager.isEmpty(clicked);

            if (AlchemyManager.isEmpty(cursor)) {
                if (emptyClicked && click == ClickType.NUMBER_KEY) {
                    AlchemyManager.scheduleCheck(player, stand);
                    return;
                }

                AlchemyManager.scheduleCheck(player, stand);
            } else if (emptyClicked) {
                if (AlchemyManager.isValidIngredient(player, cursor)) {
                    int amount = cursor.getAmount();

                    if (click == ClickType.LEFT || (click == ClickType.RIGHT && amount == 1)) {
                        event.setCancelled(true);
                        event.setCurrentItem(cursor.clone());
                        event.setCursor(null);

                        AlchemyManager.scheduleUpdate(inventory);
                        AlchemyManager.scheduleCheck(player, stand);
                    } else if (click == ClickType.RIGHT) {
                        event.setCancelled(true);

                        ItemStack one = cursor.clone();
                        one.setAmount(1);

                        ItemStack rest = cursor.clone();
                        rest.setAmount(amount - 1);

                        event.setCurrentItem(one);
                        event.setCursor(rest);

                        AlchemyManager.scheduleUpdate(inventory);
                        AlchemyManager.scheduleCheck(player, stand);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        HumanEntity whoClicked = event.getWhoClicked();

        if (!event.getInventorySlots().contains(AlchemyManager.INGREDIENT_SLOT)) {
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack ingredient = ((BrewerInventory) inventory).getIngredient();

        if (AlchemyManager.isEmpty(ingredient) || ingredient.isSimilar(cursor)) {
            Player player = (Player) whoClicked;

            if (AlchemyManager.isValidIngredient(player, cursor)) {
                // Not handled: dragging custom ingredients over ingredient slot (does not trigger any event)
                AlchemyManager.scheduleCheck(player, (BrewingStand) holder);
                return;
            }

            event.setCancelled(true);
            AlchemyManager.scheduleUpdate(inventory);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
        Inventory inventory = event.getDestination();

        if (!(inventory instanceof BrewerInventory)) {
            return;
        }

        InventoryHolder holder = inventory.getHolder();

        if (!(holder instanceof BrewingStand)) {
            return;
        }

        ItemStack item = event.getItem();

        if (item.getType() != Material.POTION) {
            event.setCancelled(true);
            return;
        }

        if (item.getType() == Material.POTION) {
            event.setCancelled(true);
            return;
        }

        if (AlchemyManager.isValidIngredient(null, item)) {
            AlchemyManager.scheduleCheck(null, (BrewingStand) holder);
        }
    }
}
