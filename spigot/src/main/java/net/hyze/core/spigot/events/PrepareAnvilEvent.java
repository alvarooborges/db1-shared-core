package net.hyze.core.spigot.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class PrepareAnvilEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private ItemStack result;
    private InventoryView transaction;

    public PrepareAnvilEvent(InventoryView inventory, ItemStack result) {
        transaction = inventory;
        this.result = result;
    }

    public AnvilInventory getInventory() {
        return (AnvilInventory) transaction.getTopInventory();
    }

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
        getInventory().setItem(2, this.result);
    }

    public InventoryView getView() {
        return transaction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
