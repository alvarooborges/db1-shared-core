package net.hyze.core.spigot.misc.utils;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import net.hyze.core.shared.messages.MessageUtils;
import net.md_5.bungee.api.ChatColor;
import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

public class ItemBuilder implements Cloneable {

    private ItemStack stack;
    private ItemMeta meta;

    public ItemBuilder() {
        this(Material.AIR);
    }

    public ItemBuilder(Material type) {
        this(type, (short) 0);
    }

    public ItemBuilder(Material type, int amount) {
        this(type, amount, (short) 0);
    }

    public ItemBuilder(Material type, short damage) {
        this(type, 1, damage);
    }

    public ItemBuilder(Material type, int amount, short damage) {
        this(new ItemStack(type, amount, damage));
    }

    public ItemBuilder(ItemStack itemStack) {
        this(itemStack, false);
    }

    public ItemBuilder(ItemStack stack, boolean keepOriginal) {
        this.stack = keepOriginal ? stack : stack.clone();
        this.meta = this.stack.getItemMeta();
    }

    public static ItemBuilder of(Material type) {
        return new ItemBuilder(type);
    }

    public static ItemBuilder of(Material type, int amount) {
        return new ItemBuilder(type, amount);
    }

    public static ItemBuilder of(Material type, short damage) {
        return new ItemBuilder(type, damage);
    }

    public static ItemBuilder of(Material type, int amount, short damage) {
        return new ItemBuilder(type, amount, damage);
    }

    public static ItemBuilder of(ItemStack itemStack, boolean keepOriginal) {
        return new ItemBuilder(itemStack, keepOriginal);
    }

    public static ItemBuilder of(ItemStack itemStack) {
        return of(itemStack, false);
    }

    public ItemBuilder type(final Material material) {
        make().setType(material);
        return this;
    }

    public Material type() {
        return make().getType();
    }

    public ItemBuilder amount(final Integer itemAmt) {
        make().setAmount(itemAmt);
        return this;
    }

    public ItemBuilder name(final String name) {
        meta().setDisplayName(MessageUtils.translateColorCodes(name));
        make().setItemMeta(meta());
        return this;
    }

    public String name() {
        return meta().getDisplayName();
    }

    public ItemBuilder lore(String... lore) {
        return lore(false, lore);
    }

    public ItemBuilder lore(boolean override, String... lore) {
        LinkedList<String> lines = Arrays.asList(lore).stream()
                .map(targetLore -> MessageUtils.translateColorCodes(targetLore))
                .collect(Collectors.toCollection(LinkedList::new));

        if (!override) {
            List<String> oldLines = meta().getLore();

            if (oldLines != null && !oldLines.isEmpty()) {
                lines.addAll(0, oldLines);
            }
        }

        java.util.regex.Pattern COLOR_PATTERN = java.util.regex.Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-F]");
        java.util.regex.Pattern START_COLOR_PATTERN = java.util.regex.Pattern.compile("^(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-F].*$");

        for (int i = 0; i < lines.size() - 1; i++) {
            String line = lines.get(i);

            if (line == null || line.isEmpty()) {
                continue;
            }

            Matcher nextMatcher = START_COLOR_PATTERN.matcher(lines.get(i + 1));

            if (nextMatcher.find()) {
                continue;
            }

            Matcher currentMatcher = COLOR_PATTERN.matcher(line);

            if (currentMatcher.find()) {
                String lastColor = currentMatcher.group(currentMatcher.groupCount());

                lines.set(i + 1, lastColor + lines.get(i + 1));
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line != null && !line.isEmpty() && !START_COLOR_PATTERN.matcher(line).find()) {
                lines.set(i, ChatColor.GRAY + line);
            }

            if (ChatColor.stripColor(line).isEmpty()) {
                lines.set(i, "");
            }
        }

        meta().setLore(lines);
        make().setItemMeta(meta());
        return this;
    }

    public List<String> lore() {
        return meta().getLore() == null ? Collections.emptyList() : Lists.newArrayList(meta().getLore());
    }

    public short durability() {
        return make().getDurability();
    }

    public ItemBuilder durability(final int durability) {
        make().setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        make().setData(new MaterialData(make().getType(), (byte) data));
        return this;
    }

    public ItemBuilder patterns(List<Pattern> patterns) {
        if (make().getType() == Material.BANNER) {
            BannerMeta meta = (BannerMeta) meta();
            meta.setPatterns(patterns);
            make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder glowing(boolean glowing) {
        if (make().getType().equals(Material.GOLDEN_APPLE)) {
            durability((short) (glowing ? 1 : 0));
            make().setItemMeta(meta());
            return this;
        }

        if (enchantments().isEmpty()) {
            if (glowing) {
                nbt("ench", new NBTTagList());
            } else {
                removeNbt("ench");
            }
        }

        make().setItemMeta(meta());
        return this;
    }

    public ItemBuilder clearFlags(ItemFlag... flags) {
        if (flags == null || flags.length == 0) {
            flags = ItemFlag.values();
        }

        meta().removeItemFlags(flags);
        make().setItemMeta(meta());

        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        meta().addItemFlags(flags);
        make().setItemMeta(meta());
        return this;
    }

    public Set<ItemFlag> flags() {
        return meta().getItemFlags();
    }

    public ItemBuilder persistent(boolean value) {
        meta.spigot().setPersistent(false);
        make().setItemMeta(meta());
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        make().addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment) {
        make().addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder enchantments(final Enchantment[] enchantments, final int level) {
        make().getEnchantments().clear();
        for (Enchantment enchantment : enchantments) {
            make().addUnsafeEnchantment(enchantment, level);
        }
        return this;
    }

    public ItemBuilder enchantments(final Enchantment[] enchantments) {
        make().getEnchantments().clear();
        for (Enchantment enchantment : enchantments) {
            make().addUnsafeEnchantment(enchantment, 1);
        }
        return this;
    }

    public ItemBuilder clearEnchantment(final Enchantment enchantment) {
        if (meta().hasEnchant(enchantment)) {
            meta().removeEnchant(enchantment);
        }

        return this;
    }

    public ItemBuilder clearEnchantments() {
        make().getEnchantments().clear();
        return this;
    }

    public Map<Enchantment, Integer> enchantments() {
        return make().getEnchantments();
    }

    public ItemBuilder clearLore(final String lore) {
        if (meta().getLore().contains(lore)) {
            meta().getLore().remove(lore);
        }
        make().setItemMeta(meta());
        return this;
    }

    public ItemBuilder clearLores() {
        if (meta().getLore() != null) {
            meta().getLore().clear();
        }
        make().setItemMeta(meta());
        return this;
    }

    public ItemBuilder effect(PotionEffect potionEffect, boolean overwrite) {
        if (meta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta();
            potionMeta.addCustomEffect(potionEffect, overwrite);

            make().setItemMeta(potionMeta);
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (make().getType() == Material.LEATHER_HELMET
                || make().getType() == Material.LEATHER_CHESTPLATE
                || make().getType() == Material.LEATHER_LEGGINGS
                || make().getType() == Material.LEATHER_BOOTS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) meta();
            meta.setColor(color);
            make().setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder color(DyeColor color) {

        if (make().getType() == Material.BANNER) {
            BannerMeta meta = (BannerMeta) meta();
            meta.setBaseColor(color);
            make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder clearColor() {
        if (make().getType() == Material.LEATHER_HELMET
                || make().getType() == Material.LEATHER_CHESTPLATE
                || make().getType() == Material.LEATHER_LEGGINGS
                || make().getType() == Material.LEATHER_BOOTS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) meta();
            meta.setColor(null);
            make().setItemMeta(meta);
        }

        if (make().getType() == Material.BANNER) {
            BannerMeta meta = (BannerMeta) meta();
            meta.setBaseColor(null);
            make().setItemMeta(meta);
        }

        return this;
    }

    public ItemBuilder skullOwner(final String name) {
        if (make().getType() == Material.SKULL_ITEM && make().getDurability() == (byte) 3) {
            SkullMeta skullMeta = (SkullMeta) meta();
            skullMeta.setOwner(name);
            make().setItemMeta(meta());
        }
        return this;
    }

    public ItemBuilder skull(Player player) {
        if (make().getType() == Material.SKULL_ITEM && make().getDurability() == (byte) 3) {
            SkullMeta skullMeta = (SkullMeta) meta();
            skullMeta.setOwner("CustomHead");

            WrappedGameProfile playerProfile = WrappedGameProfile.fromPlayer(player);
            WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), null);

            gameProfile.getProperties().putAll("textures", playerProfile.getProperties().get("textures"));
            Field profileField = null;

            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }

            profileField.setAccessible(true);

            try {
                profileField.set(skullMeta, gameProfile.getHandle());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            make().setItemMeta(skullMeta);
        }

        return this;
    }

    public ItemBuilder skullUrl(String id) {
        if (make().getType() == Material.SKULL_ITEM && make().getDurability() == (byte) 3) {
            SkullMeta skullMeta = (SkullMeta) meta();
            skullMeta.setOwner("CustomHead");

            WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.randomUUID(), null);
            byte[] encodedData;

            if (id.startsWith("http://") || id.startsWith("https://")) {
                encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", id).getBytes());
            } else {
                encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", id).getBytes());
            }

            gameProfile.getProperties().put("textures", new WrappedSignedProperty("textures", new String(encodedData), null));
            Field profileField = null;

            try {
                profileField = skullMeta.getClass().getDeclaredField("profile");
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }

            profileField.setAccessible(true);

            try {
                profileField.set(skullMeta, gameProfile.getHandle());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            make().setItemMeta(skullMeta);
        }

        return this;
    }

    private void nbt(Consumer<NBTTagCompound> consumer) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(make());

        NBTTagCompound compound = (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();

        consumer.accept(compound);

        nmsCopy.setTag(compound);

        meta = CraftItemStack.asBukkitCopy(nmsCopy).getItemMeta();
        make().setItemMeta(meta());
    }

    private <T> T nbt(Function<NBTTagCompound, T> function) {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(make());

        NBTTagCompound compound = (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();

        return function.apply(compound);
    }

    public boolean hasNbt(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag);
        });
    }

    public void removeNbt(String tag) {
        nbt(compound -> {
            compound.remove(tag);
        });
    }

    public ItemBuilder nbt(String tag, NBTBase value) {
        nbt(compound -> {
            compound.set(tag, value);
        });

        return this;
    }

    public ItemBuilder nbt(String tag, int value) {
        nbt(compound -> {
            compound.setInt(tag, value);
        });

        return this;
    }

    public ItemBuilder nbt(String tag, float value) {
        nbt(compound -> {
            compound.setFloat(tag, value);
        });

        return this;
    }

    public ItemBuilder nbt(String tag, boolean value) {
        nbt(compound -> {
            compound.setBoolean(tag, value);
        });

        return this;
    }

    public ItemBuilder nbt(String tag, long value) {
        nbt(compound -> {
            compound.setLong(tag, value);
        });

        return this;
    }

    public ItemBuilder nbt(String tag, String value) {
        nbt(compound -> {
            compound.setString(tag, value);
        });

        return this;
    }

    public ItemBuilder nbt(String tag, UUID value) {
        nbt(compound -> {
            String leastTag = String.format("%s-UUIDLEAST", tag);
            String mostTag = String.format("%s-UUIDMOST", tag);

            compound.setLong(mostTag, value.getMostSignificantBits());
            compound.setLong(leastTag, value.getLeastSignificantBits());
        });

        return this;
    }

    public String nbtString(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag) ? compound.getString(tag) : null;
        });
    }

    public Integer nbtInt(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag) ? compound.getInt(tag) : null;
        });
    }

    public Float nbtFloat(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag) ? compound.getFloat(tag) : null;
        });
    }

    public Long nbtLong(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag) ? compound.getLong(tag) : null;
        });
    }

    public Double nbtDouble(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag) ? compound.getDouble(tag) : null;
        });
    }

    public Boolean nbtBoolean(String tag) {
        return nbt(compound -> {
            return compound.hasKey(tag) ? compound.getBoolean(tag) : null;
        });
    }

    public UUID nbtUUID(String tag) {
        return nbt(compound -> {
            String leastTag = String.format("%s-UUIDLEAST", tag);
            String mostTag = String.format("%s-UUIDMOST", tag);

            return compound.hasKey(leastTag) && compound.hasKey(mostTag) ? new UUID(compound.getLong(mostTag), compound.getLong(leastTag)) : null;
        });
    }

    public NBTTagList nbtList(String tag) {
        return nbt(compound -> {
            NBTBase base = compound.get(tag);
            return base instanceof NBTTagList ? (NBTTagList) base : new NBTTagList();
        });
    }

    public NBTTagCompound nbtTag(String tag) {
        return nbt(compound -> {
            return compound.getCompound(tag);
        });
    }

    public NBTTagCompound nbt() {
        net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(make());

        return (nmsCopy.hasTag()) ? nmsCopy.getTag() : new NBTTagCompound();
    }

    public ItemMeta meta() {
        return meta;
    }

    public ItemStack make() {
        return stack;
    }

    @Override
    public ItemBuilder clone() {
        return new ItemBuilder(this.make().clone());
    }
}
