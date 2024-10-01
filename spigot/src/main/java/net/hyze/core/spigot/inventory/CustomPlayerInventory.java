package net.hyze.core.spigot.inventory;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.PlayerInventory;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class CustomPlayerInventory extends PlayerInventory {

    private final CraftInventory inventory = new CraftInventory(this);
    private final ItemStack[] extra = new ItemStack[5];
    private final CraftPlayer owner;
    private boolean playerOnline;

    public CustomPlayerInventory(Player p, boolean online) {
        super(((CraftPlayer) p).getHandle());
        this.owner = (CraftPlayer) p;
        this.items = player.inventory.items;
        this.armor = player.inventory.armor;
        this.playerOnline = online;

    }

    public Inventory getBukkitInventory() {
        return inventory;
    }

    private void saveOnExit() {
        if (transaction.isEmpty() && !playerOnline) {
            owner.saveData();
        }
    }

    private void linkInventory(PlayerInventory inventory) {
        inventory.items = this.items;
        inventory.armor = this.armor;
    }

    public void playerOnline(Player player) {
        if (!playerOnline) {
            CraftPlayer p = (CraftPlayer) player;
            linkInventory(p.getHandle().inventory);
            p.saveData();
            playerOnline = true;
        }
    }

    public void playerOffline() {
        playerOnline = false;
        owner.loadData();
        linkInventory(owner.getHandle().inventory);
        saveOnExit();
    }

//    public void onClick(EnumGroup userGroup, InventoryClickEvent event) {
//        if (!userGroup.includes(EnumGroup.MANAGER)) {
//            event.setCancelled(true);
//        }
//    }
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

//    public void onDrag(EnumGroup userGroup, InventoryDragEvent event) {
//        if (!userGroup.includes(EnumGroup.MANAGER)) {
//            event.setCancelled(true);
//        }
//    }
    public void onDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onClose(CraftHumanEntity who) {
        super.onClose(who);
        this.saveOnExit();
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] C = new ItemStack[getSize()];
        System.arraycopy(items, 0, C, 0, items.length);
        System.arraycopy(armor, 0, C, items.length, armor.length);
        return C;
    }

    @Override
    public int getSize() {
        return super.getSize() + 5;
    }

    @Override
    public ItemStack getItem(int i) {
        ItemStack[] is = this.items;

        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        return is[i];
    }

    @Override
    public ItemStack splitStack(int i, int j) {
        ItemStack[] is = this.items;

        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        if (is[i] != null) {
            ItemStack itemstack;

            if (is[i].count <= j) {
                itemstack = is[i];
                is[i] = null;
                return itemstack;
            } else {
                itemstack = is[i].cloneItemStack();
                is[i].count -= j;
                itemstack.count -= j;
                if (is[i].count == 0) {
                    is[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack splitWithoutUpdate(int i) {
        ItemStack[] is = this.items;

        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        if (is[i] != null) {
            ItemStack itemstack = is[i];

            is[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        ItemStack[] is = this.items;

        if (i >= is.length) {
            i -= is.length;
            is = this.armor;
        } else {
            i = getReversedItemSlotNum(i);
        }

        if (i >= is.length) {
            i -= is.length;
            is = this.extra;
        } else if (is == this.armor) {
            i = getReversedArmorSlotNum(i);
        }

        // Effects
        if (is == this.extra) {
            owner.getHandle().drop(itemStack, true);
            itemStack = null;
        }

        is[i] = itemStack;

        owner.getHandle().defaultContainer.b();
    }

    private int getReversedItemSlotNum(int i) {
        if (i >= 27) {
            return i - 27;
        } else {
            return i + 9;
        }
    }

    private int getReversedArmorSlotNum(int i) {
        if (i == 0) {
            return 3;
        }
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 1;
        }
        if (i == 3) {
            return 0;
        } else {
            return i;
        }
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public void update() {
        super.update();
        player.inventory.update();
    }
}
