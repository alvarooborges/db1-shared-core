package net.hyze.core.spigot.misc.enchantments.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentSlot;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;

import java.util.Map;
import java.util.Set;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class DragonLoreCustomEnchantment extends CustomEnchantment {

    public static final String KEY = "custom_enchantment_dragon_lore";
    public static final String NBT_TAG_AMPLIFIED_KEY = "dragon_lore_amplified";

    public static final Set<Integer> ENCHANTMENTS_BYPASS = Sets.newHashSet(
            Enchantment.SILK_TOUCH.getId(),
            Enchantment.LOOT_BONUS_MOBS.getId()
    );

    public static final Set<String> CUSTOM_ENCHANTMENTS_BYPASS = Sets.newHashSet(
            DragonLoreCustomEnchantment.KEY
    );

    public DragonLoreCustomEnchantment() {
        super(KEY);
    }

    @Override
    public String getDisplayName() {
        return "Sabedoria do Dragão";
    }

    @Override
    public CustomEnchantmentSlot[] getSlots() {
        return new CustomEnchantmentSlot[]{
                CustomEnchantmentSlot.ARMOR,
                CustomEnchantmentSlot.WEAPON
        };
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "&7Aumenta o nível de todos os",
                "&7outros encantamentos do",
                "&7seu item em 1."
        };
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected void onApply(ItemStack stack, int lvl) {
        ItemBuilder builder = ItemBuilder.of(stack, true);

        Map<Enchantment, Integer> enchantments = ImmutableMap.copyOf(builder.enchantments());
        Set<Enchantment> amplifiedEnchantments = Sets.newHashSet();

        enchantments.forEach((enchantment, level) -> {
            if (!ENCHANTMENTS_BYPASS.contains(enchantment.getId()) && level < enchantment.getMaxLevel() + 1) {
                builder.clearEnchantment(enchantment);
                builder.enchantment(enchantment, level + 1);
                amplifiedEnchantments.add(enchantment);
            }
        });

        Map<CustomEnchantment, Integer> customEnchantments = CustomEnchantmentUtil.getEnchantments(builder.make());
        Set<CustomEnchantment> amplifiedCustomEnchantments = Sets.newHashSet();

        customEnchantments.forEach((customEnchantment, level) -> {
            if (!CUSTOM_ENCHANTMENTS_BYPASS.contains(customEnchantment.getKey()) && level < customEnchantment.getMaxLevel() + 1) {
                customEnchantment.apply(stack, level + 1);
                amplifiedCustomEnchantments.add(customEnchantment);
            }
        });

        tagItem(stack, amplifiedEnchantments, amplifiedCustomEnchantments);
    }

    @Override
    protected void onRemove(ItemStack stack) {
        ItemBuilder builder = ItemBuilder.of(stack, true);

        Map<Enchantment, Integer> enchantments = ImmutableMap.copyOf(builder.enchantments());
        Set<Enchantment> amplifiedEnchantments = getAmplifiedEnchantments(builder.make());

        enchantments.forEach((enchantment, level) -> {
            if (!ENCHANTMENTS_BYPASS.contains(enchantment.getId())
                    && amplifiedEnchantments.contains(enchantment)) {
                builder.clearEnchantment(enchantment);
                builder.enchantment(enchantment, level - 1);
            }
        });

        Map<CustomEnchantment, Integer> customEnchantments = CustomEnchantmentUtil.getEnchantments(builder.make());
        Set<CustomEnchantment> amplifiedCustomEnchantments = getAmplifiedCustomEnchantments(builder.make());

        customEnchantments.forEach((customEnchantment, level) -> {
            if (!CUSTOM_ENCHANTMENTS_BYPASS.contains(customEnchantment.getKey())
                    && amplifiedCustomEnchantments.contains(customEnchantment)) {
                customEnchantment.apply(stack, level - 1);
            }
        });

        untagItem(stack);
    }

    private void tagItem(ItemStack stack, Set<Enchantment> amplifiedEnchantments, Set<CustomEnchantment> amplifiedCustomEnchantments) {
        ItemBuilder builder = ItemBuilder.of(stack, true);

        NBTTagCompound compound = new NBTTagCompound();

        // Encantamentos vanilla
        NBTTagList enchantmentsList = new NBTTagList();

        amplifiedEnchantments.forEach(enchantment -> {
            enchantmentsList.add(new NBTTagString(enchantment.getName()));
        });

        compound.set("enchantments", enchantmentsList);

        // Encantamentos Custom
        NBTTagList customEnchantmentsList = new NBTTagList();

        amplifiedCustomEnchantments.forEach(customEnchantment -> {
            customEnchantmentsList.add(new NBTTagString(customEnchantment.getKey()));
        });

        compound.set("custom_enchantments", customEnchantmentsList);

        // Definindo tag
        builder.nbt(NBT_TAG_AMPLIFIED_KEY, compound);
    }

    private void untagItem(ItemStack stack) {
        ItemBuilder builder = ItemBuilder.of(stack, true);
        builder.removeNbt(NBT_TAG_AMPLIFIED_KEY);
    }

    private Set<Enchantment> getAmplifiedEnchantments(ItemStack stack) {
        Set<Enchantment> out = Sets.newHashSet();

        ItemBuilder builder = ItemBuilder.of(stack);

        if (builder.nbt().hasKey(NBT_TAG_AMPLIFIED_KEY)) {
            NBTTagCompound compound = builder.nbt().getCompound(NBT_TAG_AMPLIFIED_KEY);

            if (compound.hasKey("enchantments") && compound.get("enchantments") instanceof NBTTagList) {
                NBTTagList enchantmentsList = (NBTTagList) compound.get("enchantments");

                for (int i = 0; i < enchantmentsList.size(); i++) {
                    Enchantment enchantment = Enchantment.getByName(enchantmentsList.getString(i));

                    if (enchantment != null) {
                        out.add(enchantment);
                    }
                }
            }
        }

        return out;
    }

    private Set<CustomEnchantment> getAmplifiedCustomEnchantments(ItemStack stack) {
        Set<CustomEnchantment> out = Sets.newHashSet();

        ItemBuilder builder = ItemBuilder.of(stack);

        if (builder.nbt().hasKey(NBT_TAG_AMPLIFIED_KEY)) {
            NBTTagCompound compound = builder.nbt().getCompound(NBT_TAG_AMPLIFIED_KEY);

            if (compound.hasKey("custom_enchantments") && compound.get("custom_enchantments") instanceof NBTTagList) {
                NBTTagList customEnchantmentsList = (NBTTagList) compound.get("custom_enchantments");

                for (int i = 0; i < customEnchantmentsList.size(); i++) {
                    CustomEnchantment enchantment = CustomEnchantmentRegistry.get(customEnchantmentsList.getString(i));

                    if (enchantment != null) {
                        out.add(enchantment);
                    }
                }
            }
        }

        return out;
    }
}
