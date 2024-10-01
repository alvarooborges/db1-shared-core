package net.hyze.core.spigot.misc.enchantments.triggers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.EventBus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TriggerFactory {

    public static <E extends Event, T extends EnchantmentTrigger<E>> void post(Player player, E event, Class<T> clazz) {
        List<ItemStack> items = TriggerFactory.getPlayersItems(player);

        Multimap<CustomEnchantment, ItemStack> enchantments = HashMultimap.create();

        for (ItemStack item : items) {
            Map<CustomEnchantment, Integer> map = CustomEnchantmentUtil.getEnchantments(item);

            for (CustomEnchantment enchantment : map.keySet()) {
                enchantments.put(enchantment, item);
            }
        }

        for (Map.Entry<CustomEnchantment, Collection<ItemStack>> entry : enchantments.asMap().entrySet()) {
            CustomEnchantment enchantment = entry.getKey();
            Collection<ItemStack> items0 = entry.getValue();

            for (ItemStack item : items0) {
                EnchantmentTrigger trigger = post(player, event, item, enchantment, clazz);

                if (trigger != null) {
                    if (trigger.isStopPropagation()) {
                        break;
                    }
                }
            }
        }
    }

    public static <E extends Event, T extends EnchantmentTrigger<E>> void post(Player player, E event, ItemStack stack, Class<T> clazz) {
        Map<CustomEnchantment, Integer> map = CustomEnchantmentUtil.getEnchantments(stack);

        for (CustomEnchantment enchantment : map.keySet()) {
            post(player, event, stack, enchantment, clazz);
        }
    }

    public static <E extends Event, T extends EnchantmentTrigger<E>> T post(Player player, E event, ItemStack item, CustomEnchantment enchantment, Class<T> clazz) {
        try {
            int level = CustomEnchantmentUtil.getEnchantmentLevel(item, enchantment);

            if (level > 0) {
                EventBus bus = CustomEnchantmentRegistry.getEventBus(enchantment);

                EnchantmentTrigger trigger = clazz.getConstructor(event.getClass(), Player.class, ItemStack.class, int.class)
                        .newInstance(event, player, item, level);

                bus.post(trigger);

                return (T) trigger;
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static List<ItemStack> getPlayersItems(Player player) {
        List<ItemStack> items = Lists.newArrayList();

        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
            items.add(player.getItemInHand());
        }

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                items.add(armor);
            }
        }

        return items;
    }
}
