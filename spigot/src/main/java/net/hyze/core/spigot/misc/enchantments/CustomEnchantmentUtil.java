package net.hyze.core.spigot.misc.enchantments;

import com.google.common.collect.Maps;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomEnchantmentUtil {

    public static final String NBT_LIST_KEY = "custom_enchantments_list";

    public static boolean hasEnchantment(ItemStack stack, CustomEnchantment item) {
        if (item == null) {
            return false;
        }

        return getEnchantments(stack).containsKey(item);
    }

    public static int getEnchantmentLevel(ItemStack stack, CustomEnchantment item) {
        return getEnchantments(stack).getOrDefault(item, 0);
    }

    public static void untagItem(ItemStack stack, CustomEnchantment enchantment) {
        ItemBuilder builder = ItemBuilder.of(stack, true);

        NBTTagList oldList = builder.nbtList(NBT_LIST_KEY);

        NBTTagList newList = new NBTTagList();

        for (int i = 0; i < oldList.size(); i++) {
            NBTTagCompound compound = oldList.get(i);

            if (!compound.hasKey("key") || enchantment.getKey().equals(compound.getString("key"))) {
                continue;
            }

            newList.add(compound);
        }

        builder.nbt(NBT_LIST_KEY, newList);

        if (builder.enchantments().isEmpty() && getEnchantments(stack).isEmpty()) {
            builder.removeNbt("ench");
        }
    }

    public static void tagItem(ItemStack stack, CustomEnchantment enchantment, int level) {
        ItemBuilder builder = ItemBuilder.of(stack, true);

        NBTTagList oldList = builder.nbtList(NBT_LIST_KEY);

        NBTTagList newList = new NBTTagList();

        // Evitando tag duplicada
        for (int i = 0; i < oldList.size(); i++) {
            NBTTagCompound compound = oldList.get(i);

            if (!compound.hasKey("key") || !compound.hasKey("level")
                    || !compound.hasKey("display_name") || enchantment.getKey().equals(compound.getString("key"))) {
                continue;
            }

            newList.add(compound);
        }

        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("key", enchantment.getKey());
        compound.setInt("level", level);

        compound.setString("display_name", ChatColor.stripColor(MessageUtils.translateColorCodes(enchantment.getDisplayName())));

        newList.add(compound);

        builder.nbt(NBT_LIST_KEY, newList);

        if (builder.enchantments().isEmpty()) {
            builder.nbt("ench", new NBTTagList());
        }
    }

    public static NBTTagCompound getNBTTagCompound(ItemStack stack, CustomEnchantment enchantment) {
        ItemBuilder builder = ItemBuilder.of(stack);

        NBTTagList list = builder.nbtList(NBT_LIST_KEY);

        for (int i = 0; i < list.size(); i++) {
            NBTTagCompound compound = list.get(i);

            if (!compound.hasKey("key") || !compound.hasKey("level") || !compound.hasKey("display_name")) {
                continue;
            }

            if (enchantment.getKey().equals(compound.getString("key"))) {
                return compound;
            }
        }

        return null;
    }

    public static Map<CustomEnchantment, Integer> getEnchantments(ItemStack item) {
        ItemBuilder builder = ItemBuilder.of(item);

        Map<CustomEnchantment, Integer> out = Maps.newHashMap();

        if (builder.hasNbt(NBT_LIST_KEY)) {
            NBTTagList list = builder.nbtList(NBT_LIST_KEY);

            for (int i = 0; i < list.size(); i++) {
                NBTTagCompound compound = list.get(i);

                if (!compound.hasKey("key") || !compound.hasKey("level") || !compound.hasKey("display_name")) {
                    continue;
                }

                CustomEnchantment enchantment = CustomEnchantmentRegistry.get(compound.getString("key"));

                if (enchantment == null) {
                    continue;
                }

                if (compound.getInt("level") > 0) {
                    out.put(enchantment, compound.getInt("level"));
                }
            }
        }

        return out;
    }
}
