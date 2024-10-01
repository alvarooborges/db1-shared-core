package net.hyze.core.spigot.misc.customitem.data;

import com.google.common.collect.Lists;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantment;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentRegistry;
import net.hyze.core.spigot.misc.enchantments.CustomEnchantmentUtil;
import net.hyze.core.spigot.misc.enchantments.data.DragonLoreCustomEnchantment;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.hyze.core.spigot.misc.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.greenrobot.eventbus.Subscribe;

public final class ExtractionRuneItem extends CustomItem {

    public static String KEY = "custom_item_extration_rune";

    @Getter
    private final ItemBuilder itemBuilder;

    public ExtractionRuneItem() {
        super(KEY);

        this.itemBuilder = ItemBuilder.of(Material.NAME_TAG)
                .glowing(true)
                .name("&6" + this.getDisplayName())
                .lore(
                        "&7Ao utilizar esta runa em um item, todos os",
                        "&7seus encantamentos são transformados",
                        "&7em livros.",
                        "",
                        "&eComo utilizar?",
                        "&fClique com esta runa em cima do item que",
                        "&fvocê deseja extrair os encantamentos."
                );
    }

    @Override
    public String getDisplayName() {
        return "Runa de Extração";
    }

    @Subscribe
    public void on(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack cursor = event.getCursor();
        if (cursor == null || cursor.getType() == Material.AIR) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        if (CustomItemRegistry.getByItemStack(cursor) != this) {
            return;
        }

        ItemStack target = currentItem.clone();

        if (target.getType() == Material.ENCHANTED_BOOK || CustomItemRegistry.getByItemStack(target) != null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        Map<CustomEnchantment, Integer> customEnchantments = CustomEnchantmentUtil.getEnchantments(target);

        if (target.getEnchantments().isEmpty() && customEnchantments.isEmpty()) {
            return;
        }

        List<ItemStack> out = Lists.newArrayList();

        CustomEnchantment dragonLoreCustomEnchantment = CustomEnchantmentRegistry.get(DragonLoreCustomEnchantment.KEY);

        // Se o encantamento dragon lore estiver registrado e o item conter dragon lore
        boolean hasDragonLore = dragonLoreCustomEnchantment != null && customEnchantments.containsKey(dragonLoreCustomEnchantment);

        if (hasDragonLore) {
            int level = CustomEnchantmentUtil.getEnchantmentLevel(target, dragonLoreCustomEnchantment);
            out.add(dragonLoreCustomEnchantment.getBook(level, 1));
            dragonLoreCustomEnchantment.remove(target);
            customEnchantments = CustomEnchantmentUtil.getEnchantments(target);
        }

        for (Map.Entry<CustomEnchantment, Integer> entry : customEnchantments.entrySet()) {
            out.add(entry.getKey().getBook(entry.getValue(), 1));
            entry.getKey().remove(target);
        }

        for (Map.Entry<Enchantment, Integer> entry : target.getEnchantments().entrySet()) {
            ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
            ItemStackUtils.addBookEnchantment(stack, entry.getKey(), entry.getValue());

            out.add(stack);
            target.removeEnchantment(entry.getKey());
        }

        out.add(target);

        if (!InventoryUtils.fits(player.getInventory(), out.stream().toArray(ItemStack[]::new))) {
            Message.ERROR.send(player, "Seu inventário está cheio.");
            return;
        }

        if (cursor.getAmount() > 1) {
            cursor.setAmount(cursor.getAmount() - 1);
        } else {
            event.setCursor(null);
        }

        if (currentItem.getAmount() > 1) {
            currentItem.setAmount(currentItem.getAmount() - 1);
        } else {
            event.setCurrentItem(null);
        }

        player.getInventory().addItem(out.stream().toArray(ItemStack[]::new));
    }

    
}
