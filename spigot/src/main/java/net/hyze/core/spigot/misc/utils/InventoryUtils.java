package net.hyze.core.spigot.misc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryUtils {

    public static int getRow(int slot) {
        return ((slot + 9) / 9) - 1;
    }

    public static int getSlot(int row, int column) {
        return ((row - 1) * 9 + column) - 1;
    }

    public static int getColumn(int slot) {
        return (slot - (9 * getRow(slot)));
    }

    public static String serializeContents(ItemStack[] contents) {
        if (contents == null) {
            return null;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(contents.length);

            for (int i = 0; i < contents.length; i++) {
                dataOutput.writeInt(i);
                dataOutput.writeObject(contents[i]);
            }

            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ItemStack[] deserializeContents(String str) {

        if (str == null || str.isEmpty()) {
            return null;
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));

        try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            int size = dataInput.readInt();

            ItemStack[] contents = new ItemStack[size];

            for (int i = 0; i < size; i++) {
                try {
                    contents[dataInput.readInt()] = (ItemStack) dataInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return contents;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Inventory copyToInventory(ItemStack[] stacks) {
        Inventory copy = Bukkit.createInventory(null, stacks.length);

        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] != null) {
                copy.setItem(i, stacks[i].clone());
            }
        }

        return copy;
    }

    public static Inventory copy(Inventory inventory) {
        Inventory inv = Bukkit.createInventory(null, inventory.getSize(), inventory.getTitle());

        ItemStack[] orginal = inventory.getContents();
        ItemStack[] clone = new ItemStack[orginal.length];

        for (int i = 0; i < orginal.length; i++) {
            if (orginal[i] != null) {
                clone[i] = orginal[i].clone();
            }
        }

        inv.setContents(clone);

        return inv;
    }

    public static boolean fits(Inventory inventory, ItemStack... stacks) {
        Inventory clonedInventory = InventoryUtils.copy(inventory);

        for (int i = 0; i < clonedInventory.getSize(); i++) {
            if (clonedInventory.getContents()[i] != null) {
            }
        }

        ItemStack[] clone = new ItemStack[stacks.length];

        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] != null) {
                clone[i] = stacks[i].clone();
            }
        }

        return clonedInventory.addItem(clone).isEmpty();
    }

    public static void removeItemsFiltered(Inventory inventory, ItemStack item, int amount) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack targetItem = inventory.getItem(slot);

            if (targetItem == null || !item.isSimilar(targetItem)) {
                continue;
            }

            if (amount == -1) {
                inventory.setItem(slot, null);
                continue;
            }

            if (amount < targetItem.getAmount()) {
                targetItem.setAmount(targetItem.getAmount() - amount);
                return;
            } else {
                inventory.setItem(slot, null);
                amount -= targetItem.getAmount();
            }
        }
    }

    public static void removeItemsFiltered(Collection<Integer> allowedSlots, Inventory inventory, ItemStack item, int amount) {
        for (int slot : allowedSlots) {
            ItemStack targetItem = inventory.getItem(slot);

            if (targetItem == null || !item.isSimilar(targetItem)) {
                continue;
            }

            if (amount == -1) {
                inventory.setItem(slot, null);
                continue;
            }

            if (amount < targetItem.getAmount()) {
                targetItem.setAmount(targetItem.getAmount() - amount);
                return;
            } else {
                inventory.setItem(slot, null);
                amount -= targetItem.getAmount();
            }
        }
    }

    public static int countItems(Inventory inventory, ItemStack item) {
        return (int) Arrays.stream(inventory.getContents())
                .filter(itemTarget -> itemTarget.isSimilar(item))
                .count();
    }

    public static boolean subtractOne(Player player, ItemStack stack) {

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack content = player.getInventory().getContents()[i];

            if (content != null && content.isSimilar(stack)) {
                if (content.getAmount() > 1) {
                    content.setAmount(content.getAmount() - 1);
                    contents[i] = content;
                } else {
                    contents[i] = null;
                }

                player.getInventory().setContents(contents);
                player.updateInventory();
                return true;
            }
        }

        return false;
    }

    public static void subtractOneOnHand(PlayerInteractEvent event) {
        subtractOneOnHand(event.getPlayer());
    }

    public static void subtractOneOnHand(Player player) {
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        ItemStack item = player.getItemInHand();
        int amount = item.getAmount();

        if (amount > 1) {
            item.setAmount(amount - 1);
            player.setItemInHand(item);
        } else {
            item.setAmount(0);
            item.setType(Material.AIR);
            item.setData(new MaterialData(Material.AIR));
            item.setItemMeta(null);
            player.setItemInHand(new ItemStack(Material.AIR));
        }
    }

    public static void give(Player player, ItemStack... item) {
        Map<Integer, ItemStack> left = player.getInventory().addItem(item);

        player.updateInventory();

        if (left != null && !left.isEmpty()) {
            for (ItemStack l : left.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), l);
            }
        }
    }

    // Returns what it couldnt store
    // This will will abort if it couldn't store all items
    public static Map<Integer, ItemStack> addAllItems(final Inventory inventory, boolean simulate,
                                                      final ItemStack... items) {
        final Inventory fakeInventory = Bukkit.getServer()
                .createInventory(null, inventory.getType());
        fakeInventory.setContents(inventory.getContents());
        Map<Integer, ItemStack> overFlow = addOversizedItems(fakeInventory, 0, items);
        if (overFlow.isEmpty()) {
            if (!simulate) {
                addOversizedItems(inventory, 0, items);
            }
            return null;
        }
        return addOversizedItems(fakeInventory, 0, items);
    }

    // Returns what it couldnt store
    // Set oversizedStack to below normal stack size to disable oversized stacks
    private static Map<Integer, ItemStack> addOversizedItems(final Inventory inventory,
                                                             final int oversizedStacks, final ItemStack... items) {
        final Map<Integer, ItemStack> leftover = new HashMap<>();

        /*
         * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' -
         * Record the lastPartial per Material - Cache firstEmpty result
         */
        // combine items
        final ItemStack[] combined = new ItemStack[items.length];
        for (ItemStack item : items) {
            if (item == null || item.getAmount() < 1) {
                continue;
            }
            for (int j = 0; j < combined.length; j++) {
                if (combined[j] == null) {
                    combined[j] = item.clone();
                    break;
                }
                if (combined[j].isSimilar(item)) {
                    combined[j].setAmount(combined[j].getAmount() + item.getAmount());
                    break;
                }
            }
        }

        for (int i = 0; i < combined.length; i++) {
            final ItemStack item = combined[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            while (true) {
                // Do we already have a stack of it?
                final int maxAmount =
                        oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks
                                : item.getType().getMaxStackSize();
                final int firstPartial = firstPartial(inventory, item, maxAmount);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    final int firstFree = inventory.firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else // More than a single stack!
                    {
                        if (item.getAmount() > maxAmount) {
                            final ItemStack stack = item.clone();
                            stack.setAmount(maxAmount);
                            inventory.setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - maxAmount);
                        } else {
                            // Just store it
                            inventory.setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    final ItemStack partialItem = inventory.getItem(firstPartial);

                    final int amount = item.getAmount();
                    final int partialAmount = partialItem.getAmount();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    private static int firstPartial(final Inventory inventory, final ItemStack item,
                                    final int maxAmount) {
        if (item == null) {
            return -1;
        }
        final ItemStack[] stacks = inventory.getContents();
        for (int i = 0; i < stacks.length; i++) {
            final ItemStack cItem = stacks[i];
            if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item)) {
                return i;
            }
        }
        return -1;
    }

    /*

     */

    // Returns what it couldnt store
    // This will will abort if it couldn't store all items
    public static Map<Integer, ItemStack> addAllItemsFiltered(Collection<Integer> allowedSlots,
                                                              final Inventory inventory, final ItemStack... items) {
        return addOversizedItems(allowedSlots, inventory, 0, items);
    }

    // Returns what it couldnt store
    // Set oversizedStack to below normal stack size to disable oversized stacks
    private static Map<Integer, ItemStack> addOversizedItems(Collection<Integer> allowedSlots,
                                                             final Inventory inventory, final int oversizedStacks, final ItemStack... items) {
        final Map<Integer, ItemStack> leftover = new HashMap<>();

        /*
         * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' -
         * Record the lastPartial per Material - Cache firstEmpty result
         */
        // combine items
        final ItemStack[] combined = new ItemStack[items.length];
        for (ItemStack item : items) {
            if (item == null || item.getAmount() < 1) {
                continue;
            }
            for (int j = 0; j < combined.length; j++) {
                if (combined[j] == null) {
                    combined[j] = item.clone();
                    break;
                }
                if (combined[j].isSimilar(item)) {
                    combined[j].setAmount(combined[j].getAmount() + item.getAmount());
                    break;
                }
            }
        }

        for (int i = 0; i < combined.length; i++) {
            final ItemStack item = combined[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            while (true) {
                // Do we already have a stack of it?
                final int maxAmount =
                        oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks
                                : item.getType().getMaxStackSize();
                final int firstPartial = firstPartial(allowedSlots, inventory, item, maxAmount);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = -1;
                    for (int j : allowedSlots) {
                        if (inventory.getItem(j) == null) {
                            firstFree = j;
                            break;
                        }
                    }

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else // More than a single stack!
                    {
                        if (item.getAmount() > maxAmount) {
                            final ItemStack stack = item.clone();
                            stack.setAmount(maxAmount);
                            inventory.setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - maxAmount);
                        } else {
                            // Just store it
                            inventory.setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    final ItemStack partialItem = inventory.getItem(firstPartial);

                    final int amount = item.getAmount();
                    final int partialAmount = partialItem.getAmount();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    private static int firstPartial(Collection<Integer> allowedSlots, final Inventory inventory,
                                    final ItemStack item, final int maxAmount) {
        if (item == null) {
            return -1;
        }

        final ItemStack[] stacks = inventory.getContents();
        for (int i : allowedSlots) {
            final ItemStack cItem = stacks[i];
            if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item)) {
                return i;
            }
        }

        return -1;
    }

}
