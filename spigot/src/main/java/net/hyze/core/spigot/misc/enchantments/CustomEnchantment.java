package net.hyze.core.spigot.misc.enchantments;

import com.google.common.collect.Lists;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.misc.utils.TextUtil;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

@EqualsAndHashCode(of = {"key"})
@RequiredArgsConstructor
public abstract class CustomEnchantment implements Listener {

    @Getter
    public final String key;

    public abstract String getDisplayName();

    public abstract String[] getDescription();

    public abstract CustomEnchantmentSlot[] getSlots();

    public abstract int getMaxLevel();

    protected void onApply(ItemStack stack, int level) {
    }

    protected void onRemove(ItemStack stack) {
    }

    public boolean canEnchant(ItemStack item) {
        return Lists.newArrayList(getSlots()).stream().anyMatch(slot -> slot.canEnchant(item));
    }

    public final String getDisplayName(int level) {
        return getDisplayName() + " " + NumberUtils.toRoman(level);
    }

    public final void apply(ItemStack stack, int level) {
        if (CustomEnchantmentUtil.hasEnchantment(stack, this)) {
            remove(stack);
        }

        ItemBuilder builder = ItemBuilder.of(stack, true);

        List<String> lore = builder.lore();

        builder.clearLores()
                .lore(true, this.getDisplayName(level))
                .lore(lore.stream().toArray(String[]::new));

        CustomEnchantmentUtil.tagItem(stack, this, level);

        this.onApply(stack, level);
    }

    public final void remove(ItemStack stack) {
        if (!CustomEnchantmentUtil.hasEnchantment(stack, this)) {
            return;
        }

        ItemBuilder builder = ItemBuilder.of(stack, true);

        NBTTagCompound compound = CustomEnchantmentUtil.getNBTTagCompound(stack, this);

        List<String> lore = builder.lore();

        lore.removeIf(line -> {
            line = ChatColor.stripColor(MessageUtils.translateColorCodes(line));

            return line.startsWith(compound.getString("display_name"));
        });

        builder.lore(true, lore.stream().toArray(String[]::new));

        CustomEnchantmentUtil.untagItem(stack, this);

        this.onRemove(stack);
    }

    public final ItemStack getBook(int level, int amount) {
        ItemBuilder item = ItemBuilder.of(Material.ENCHANTED_BOOK)
                .name(ChatColor.GOLD + getDisplayName(level))
                .amount(amount)
                .lore(getDescription())
                .lore("");

        List<String> slots = Arrays.asList(getSlots()).stream()
                .map(CustomEnchantmentSlot::getName)
                .collect(Collectors.toList());

        item.lore(String.format("&fEncanta: &a%s.", TextUtil.join(slots, ", ", " e ")))
                .lore("&eUtilize uma bigorna para encantar.");

        ItemStack result = item.make();

        CustomEnchantmentUtil.tagItem(result, this, level);

        return result;
    }
}
