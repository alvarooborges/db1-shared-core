package net.hyze.core.spigot.commands.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.customcraft.Craft;
import net.hyze.core.spigot.misc.customcraft.CraftSlot;
import net.hyze.core.spigot.misc.customcraft.CustomCraft;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.customitem.data.compacted.BlazeRodCompacted;
import net.hyze.core.spigot.misc.customitem.data.compacted.BoneCompacted;
import net.hyze.core.spigot.misc.customitem.data.compacted.RottenFleshCompacted;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import java.util.*;

public class CompactCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private final Group group;

    private final HashMap<ItemStack, Result> map = Maps.newHashMap();

    public CompactCommand(Group group) {
        super("compactar", CommandRestriction.IN_GAME);
        this.group = group;

        map.put(
                new ItemStack(Material.IRON_INGOT, 1, (byte) 0),
                new Result(new MaterialData(Material.IRON_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.GOLD_INGOT, 1, (byte) 0),
                new Result(new MaterialData(Material.GOLD_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.GOLD_NUGGET, 1, (byte) 0),
                new Result(new MaterialData(Material.GOLD_INGOT, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.REDSTONE, 1, (byte) 0),
                new Result(new MaterialData(Material.REDSTONE_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.COAL, 1, (byte) 0),
                new Result(new MaterialData(Material.COAL_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.DIAMOND, 1, (byte) 0),
                new Result(new MaterialData(Material.DIAMOND_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.EMERALD, 1, (byte) 0),
                new Result(new MaterialData(Material.EMERALD_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.INK_SACK, 1, (byte) 4),
                new Result(new MaterialData(Material.LAPIS_BLOCK, (byte) 0), 9)
        );
        map.put(
                new ItemStack(Material.SLIME_BALL, 1, (byte) 0),
                new Result(new MaterialData(Material.SLIME_BLOCK, (byte) 0), 9)
        );


        if (CustomItemRegistry.getItem(BoneCompacted.KEY) != null) {
            map.put(
                    new ItemStack(Material.BONE, 1),
                    new Result(BoneCompacted.KEY, 9)
            );

            CustomCraft.insertCraft(new Craft(
                    new ItemBuilder(CustomItemRegistry.getItem(BoneCompacted.KEY).asItemStack()),
                    ShapelessRecipe.class,
                    new CraftSlot(new MaterialData(Material.BONE, (byte) 0))
            ));
        }

        if (CustomItemRegistry.getItem(RottenFleshCompacted.KEY) != null) {
            map.put(
                    new ItemStack(Material.ROTTEN_FLESH, 1),
                    new Result(RottenFleshCompacted.KEY, 9)
            );

            CustomCraft.insertCraft(new Craft(
                    new ItemBuilder(CustomItemRegistry.getItem(RottenFleshCompacted.KEY).asItemStack()),
                    ShapelessRecipe.class,
                    new CraftSlot(new MaterialData(Material.ROTTEN_FLESH, (byte) 0))
            ));
        }

        if (CustomItemRegistry.getItem(BlazeRodCompacted.KEY) != null) {
            map.put(
                    new ItemStack(Material.BLAZE_ROD, 1),
                    new Result(BlazeRodCompacted.KEY, 9)
            );

            CustomCraft.insertCraft(new Craft(
                    new ItemBuilder(CustomItemRegistry.getItem(BlazeRodCompacted.KEY).asItemStack()),
                    ShapelessRecipe.class,
                    new CraftSlot(new MaterialData(Material.BLAZE_ROD, (byte) 0))
            ));
        }
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Inventory inventory = InventoryUtils.copy(player.getInventory());

        Set<ItemStack> checked = Sets.newHashSet();

        int count = 0;

        for (ItemStack target : inventory.getContents()) {
            if (target == null) {
                continue;
            }

            boolean anyMatch = checked.stream()
                    .anyMatch(c -> ItemStackUtils.isSimilar(c, target));

            if (anyMatch) {
                continue;
            }

            checked.add(target);

            Result targetResult = map.entrySet().stream()
                    .filter(entry -> ItemStackUtils.isSimilar(entry.getKey(), target))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            if (targetResult == null || targetResult.getResult() == null) {
                continue;
            }

            int totalItems = Arrays.stream(inventory.getContents())
                    .filter(Objects::nonNull)
                    .filter(item -> ItemStackUtils.isSimilar(item, target))
                    .mapToInt(ItemStack::getAmount)
                    .sum();

            if (totalItems < targetResult.getAmountNeeded()) {
                continue;
            }

            int leftOvers = Math.floorMod(totalItems, targetResult.getAmountNeeded());
            int used = totalItems - leftOvers;
            int resultProductAmount = used / targetResult.getAmountNeeded();

            int removed = 0;

            Inventory checkerInventory = InventoryUtils.copy(inventory);
            ItemStack[] checkerContents = checkerInventory.getContents();

            for (int i = 0; i < checkerContents.length; i++) {
                if (checkerContents[i] == null || !ItemStackUtils.isSimilar(checkerContents[i], target)) {
                    continue;
                }

                ItemStack content = checkerContents[i];

                if (content.getAmount() <= (used - removed)) {
                    checkerContents[i] = null;

                    removed += content.getAmount();

                    if (removed == used) {
                        break;
                    }

                    continue;
                }

                checkerContents[i].setAmount(content.getAmount() - (used - removed));
                break;
            }

            checkerInventory.setContents(checkerContents);

            ItemStack rawResult = targetResult.getResult();

            ItemStack result = ItemBuilder.of(rawResult)
                    .amount(rawResult.getAmount() * resultProductAmount)
                    .make();

            if (!InventoryUtils.fits(checkerInventory, result)) {
                continue;
            }

            checkerInventory.addItem(result);

            count += result.getAmount();
            inventory.setContents(checkerInventory.getContents());
        }

        if (count > 0) {
            Message.SUCCESS.send(sender, "Seu inventário foi compactado.");
            player.getInventory().setContents(inventory.getContents());
        } else {
            Message.SUCCESS.send(sender, "Nenhum item para compactar.");
        }

    }

    @Getter
    @RequiredArgsConstructor
    static class Result {

        private final ItemStack result;
        private final int amountNeeded;

        Result(MaterialData materialData, int amountNeeded) {
            this.result = new ItemStack(materialData.getItemType(), 1, materialData.getData());
            this.amountNeeded = amountNeeded;
        }

        Result(String itemCustomId, int amountNeeded) {
            CustomItem customItem = CustomItemRegistry.getItem(itemCustomId);

            if (customItem != null) {
                this.result = customItem.asItemStack();
            } else {
                this.result = null;
            }

            this.amountNeeded = amountNeeded;
        }

        public ItemStack getResult() {
            return result.clone();
        }
    }

    static class NineRecipe {
        private MaterialData data;
        private ItemStack result;

        public Craft getCraft() {
            return new Craft(
                    new ItemBuilder(result),
                    ShapedRecipe.class,
                    new CraftSlot(0, data),
                    new CraftSlot(1, data),
                    new CraftSlot(2, data),
                    new CraftSlot(3, data),
                    new CraftSlot(4, data),
                    new CraftSlot(5, data),
                    new CraftSlot(6, data),
                    new CraftSlot(7, data),
                    new CraftSlot(8, data)
            );
        }
    }
}
