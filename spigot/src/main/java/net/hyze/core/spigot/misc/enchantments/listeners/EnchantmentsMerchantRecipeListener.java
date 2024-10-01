package net.hyze.core.spigot.misc.enchantments.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.enchantments.data.DragonLoreCustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.merchant.MerchantRecipeEvent;
import java.util.Map;
import java.util.Objects;
import net.minecraft.server.v1_8_R3.Enchantment;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import net.minecraft.server.v1_8_R3.Items;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class EnchantmentsMerchantRecipeListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onLow(MerchantRecipeEvent event) {

        ItemStack first = event.getFirst();
        ItemStack second = event.getSecond();

        if (first == null || second == null || first.getType() == Material.AIR || second.getType() == Material.AIR) {
            return;
        }

        net.minecraft.server.v1_8_R3.ItemStack nmsResult = CraftItemStack.asNMSCopy(first.clone());
        net.minecraft.server.v1_8_R3.ItemStack nmsFirst = CraftItemStack.asNMSCopy(first.clone());
        net.minecraft.server.v1_8_R3.ItemStack nmsSecond = CraftItemStack.asNMSCopy(second.clone());

        boolean changed = false;
        /**
         * Inicio do Encantamentos Vanila
         *
         * Codigo baseado no sistema vanila da bigorna
         */
        boolean firstIsEnchantedBook = nmsFirst.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.h(nmsFirst).size() > 0;
        boolean secondIsEnchantedBook = nmsSecond.getItem() == Items.ENCHANTED_BOOK && Items.ENCHANTED_BOOK.h(nmsSecond).size() > 0;

//        if (nmsFirst.e() && nmsFirst.getItem().a(nmsFirst, nmsSecond)) {
//            int k = Math.min(nmsFirst.h(), nmsFirst.j() / 4);
//
//            if (k <= 0) {
//                return;
//            }
//
//            int i1 = nmsFirst.h() - k;
//
//            nmsResult.setData(i1);
//
//            event.setResult(CraftItemStack.asBukkitCopy(nmsResult));
//            return;
//        }

        if (secondIsEnchantedBook || !(nmsFirst.getItem() != nmsSecond.getItem() || !nmsFirst.e())) {

            Map<Integer, Integer> firstEnchantments = EnchantmentManager.a(nmsFirst);
            Map<Integer, Integer> secondEnchantments = EnchantmentManager.a(nmsSecond);

            for (Map.Entry<Integer, Integer> secondEntry : secondEnchantments.entrySet()) {
                int secondEnchantmentId = secondEntry.getKey();
                int secondEnchantmentLevel = secondEntry.getValue();

                Enchantment secondEnchantment = Enchantment.getById(secondEnchantmentId);

                if (secondEnchantment == null) {
                    continue;
                }

                int firstEnchantmentLevel = firstEnchantments.getOrDefault(secondEnchantmentId, 0);

                int newEnchantmentLevel;

                if (firstEnchantmentLevel == secondEnchantmentLevel) {
                    newEnchantmentLevel = secondEnchantmentLevel + 1;
                } else {
                    newEnchantmentLevel = Math.max(firstEnchantmentLevel, secondEnchantmentLevel);
                }

                boolean canEnchant = secondEnchantment.canEnchant(nmsFirst);

                if (nmsFirst.getItem() == Items.ENCHANTED_BOOK) {
                    canEnchant = true;
                }

                for (Map.Entry<Integer, Integer> firstEntry : firstEnchantments.entrySet()) {
                    if (firstEntry.getKey() != secondEnchantmentId && !secondEnchantment.a(Enchantment.getById(firstEntry.getKey()))) {
                        canEnchant = false;
                    }
                }

                if (!canEnchant) {
                    continue;
                }

//                if (secondIsEnchantedBook && !firstIsEnchantedBook) {
//                    
//                }
                if (firstEnchantmentLevel > 0 || !secondIsEnchantedBook) {
                    if (newEnchantmentLevel > secondEnchantment.getMaxLevel()) {
                        newEnchantmentLevel = Math.max(firstEnchantmentLevel, secondEnchantmentLevel);
                    }
                }

                if (!Objects.equals(firstEnchantments.get(secondEnchantmentId), newEnchantmentLevel)) {
                    firstEnchantments.put(secondEnchantmentId, newEnchantmentLevel);
                    changed = true;
                }
            }

            if (changed) {
                EnchantmentManager.a(firstEnchantments, nmsResult);
            }
        }

        ItemStack result = CraftItemStack.asBukkitCopy(nmsResult);

        /**
         * Inicio do Encantamentos Custom
         */
        boolean secondIsCustomEnchantedBook = second.getType() == Material.ENCHANTED_BOOK && CustomEnchantmentUtil.getEnchantments(second).size() > 0;

        Map<CustomEnchantment, Integer> firstCustomEnchantments = CustomEnchantmentUtil.getEnchantments(first);
        Map<CustomEnchantment, Integer> secondCustomEnchantments = CustomEnchantmentUtil.getEnchantments(second);

        if (secondIsCustomEnchantedBook || first.getType() == second.getType()
                || (!firstCustomEnchantments.isEmpty() && first.getType() != Material.ENCHANTED_BOOK)) {

            for (Map.Entry<CustomEnchantment, Integer> secondEntry : secondCustomEnchantments.entrySet()) {
                CustomEnchantment secondEnchantment = secondEntry.getKey();
                int secondEnchantmentLevel = secondEntry.getValue();

                int firstEnchantmentLevel = firstCustomEnchantments.getOrDefault(secondEnchantment, 0);

                int newEnchantmentLevel;

                if (firstEnchantmentLevel == secondEnchantmentLevel) {
                    newEnchantmentLevel = secondEnchantmentLevel + 1;
                } else {
                    newEnchantmentLevel = Math.max(firstEnchantmentLevel, secondEnchantmentLevel);
                }

                boolean canEnchant = secondEnchantment.canEnchant(result);

                if (!canEnchant) {
                    continue;
                }

                if (newEnchantmentLevel > secondEnchantment.getMaxLevel()) {
                    newEnchantmentLevel = secondEnchantment.getMaxLevel();
                }

                if (!Objects.equals(firstCustomEnchantments.get(secondEnchantment), newEnchantmentLevel)) {
                    firstCustomEnchantments.put(secondEnchantment, newEnchantmentLevel);
                    changed = true;
                }
            }

            if (changed) {
                firstCustomEnchantments.forEach((enchantment, level) -> {
                    enchantment.apply(result, level);
                });
            }
        }
        // Fim dos Encantamentos Custom

        if (changed) {
            if (CustomEnchantmentUtil.getEnchantments(result).size() + result.getEnchantments().size() > 5) {
                return;
            }

            applyDragonLore(result); // gambiarra Dragon Lore 

            if (result.getType() != Material.ENCHANTED_BOOK && first.getType() == second.getType()) {
                result.setDurability((short) Math.max((int) first.getDurability(), (int) second.getDurability()));
            }

            event.setResult(result);
        }
    }

    private void applyDragonLore(ItemStack stack) {
        CustomEnchantment dragonLoreCustomEnchantment = CustomEnchantmentRegistry.get(DragonLoreCustomEnchantment.KEY);
        if (dragonLoreCustomEnchantment != null) {
            if (CustomEnchantmentUtil.hasEnchantment(stack, dragonLoreCustomEnchantment)) {
                int level = CustomEnchantmentUtil.getEnchantmentLevel(stack, dragonLoreCustomEnchantment);

                dragonLoreCustomEnchantment.apply(stack, level);
            }
        }
    }

//    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
//    public void onNormal(MerchantRecipeEvent event) {
//
//        ItemStack first = event.getFirst();
//        ItemStack second = event.getSecond();
//
//        if (first == null || second == null || first.getType() == Material.AIR || second.getType() == Material.AIR) {
//            return;
//        }
//
//        if (first.getType() != second.getType() && second.getType() != Material.ENCHANTED_BOOK) {
//            return;
//        }
//
//        Map<CustomEnchantment, Integer> enchantments = CustomEnchantmentUtil.getEnchantments(first);
//        Map<CustomEnchantment, Integer> newEnchantments = CustomEnchantmentUtil.getEnchantments(second);
//
//        if (newEnchantments.isEmpty()) {
//            return;
//        }
//
//        if (enchantments.size() + newEnchantments.size() + first.getEnchantments().size() >= 5) {
//            return;
//        }
//
//        if (!newEnchantments.keySet().stream().allMatch(enchantment -> enchantment.canEnchant(first))) {
//            return;
//        }
//
//        ItemStack result;
//
//        if (event.getResult() != null) {
//            result = event.getResult().clone();
//        } else {
//            result = event.getFirst().clone();
//        }
//
//        newEnchantments.forEach((enchantment, level) -> {
//            enchantment.apply(result, level);
//        });
//
//        /**
//         * Dragon Lore gambiarra
//         */
//        CustomEnchantment dragonLoreCustomEnchantment;
//        if ((dragonLoreCustomEnchantment = CustomEnchantmentRegistry.get(DragonLoreCustomEnchantmentItem.KEY)) != null) {
//            if (CustomEnchantmentUtil.hasEnchantment(result, dragonLoreCustomEnchantment)) {
//                int level = CustomEnchantmentUtil.getEnchantmentLevel(result, dragonLoreCustomEnchantment);
//                dragonLoreCustomEnchantment.apply(result, level);
//            }
//        }
//
//        event.setResult(result);
//    }
}
