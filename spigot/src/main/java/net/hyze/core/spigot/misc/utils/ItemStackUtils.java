package net.hyze.core.spigot.misc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemStackUtils {

    public static void serializeNBT(NBTTagCompound compound, ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack fuelCraft = CraftItemStack.asNMSCopy(itemStack);
        if (fuelCraft != null) {
            fuelCraft.save(compound);
        }
    }

    public static ItemStack deserializeNBT(NBTTagCompound compound) {
        net.minecraft.server.v1_8_R3.ItemStack craft = net.minecraft.server.v1_8_R3.ItemStack.createStack(compound);
        return CraftItemStack.asCraftMirror(craft);
    }

    public static ItemStack addBookEnchantment(ItemStack item, Enchantment enchantment, int level) {
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(enchantment, level, true);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isSimilar(ItemStack s1, ItemStack s2) {
        if (s1 == null || s2 == null) {
            return false;
        }

        return s1.getType().equals(s2.getType())
                && s1.getDurability() == s2.getDurability()
                && s1.hasItemMeta() == s2.hasItemMeta()
                && Bukkit.getItemFactory().equals(s1.getItemMeta(), s2.getItemMeta());
    }

    public static ItemStack[] asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack[] stacks) {
        return asBukkitCopy(stacks, false);
    }

    public static ItemStack[] asBukkitCopy(net.minecraft.server.v1_8_R3.ItemStack[] stacks, boolean removeAir) {
        ItemStack[] out = new ItemStack[stacks.length];

        for (int i = 0; i < out.length; i++) {
            ItemStack stack = CraftItemStack.asBukkitCopy(stacks[i]);

            if (!removeAir) {
                out[i] = stack;
                continue;
            }

            if (stack.getType() != Material.AIR) {
                out[i] = stack;
            }
        }

        return out;
    }
}
