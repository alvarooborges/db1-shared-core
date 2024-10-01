package net.hyze.core.spigot.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.customitem.CustomItemRegistry;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.greenrobot.eventbus.Subscribe;

public class RepairCoinItem extends CustomItem {

    @Getter
    private final ItemBuilder itemBuilder;

    public RepairCoinItem() {
        super("custom_item_repair_coin");

        this.itemBuilder = ItemBuilder.of(Material.DOUBLE_PLANT)
                .glowing(true)
                .name("&6Moeda de Reparação")
                .lore(
                        "&7Ao utilizar esta moeda em um item, ele",
                        "&7será reparado completamente!",
                        "",
                        "&eComo utilizar?",
                        "&fClique com esta moeda em cima do item que",
                        "&fvocê deseja reparar."
                );
    }

    @Override
    public String getDisplayName() {
        return "&6Moeda de Reparação";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            Message.ERROR.send(event.getPlayer(), "Você deve utilizar este item em uma bigorna.");
        }
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

        if (currentItem.getDurability() > 0
                && (currentItem.getType().name().startsWith("DIAMOND_")
                || currentItem.getType().name().startsWith("IRON_")
                || currentItem.getType().name().startsWith("GOLD_")
                || currentItem.getType().name().startsWith("LEATHER_")
                || currentItem.getType() == Material.BOW)) {

            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

            if (CombatManager.isTagged(user)) {
                Message.ERROR.send(player, "Você não pode reparar itens enquanto estiver em combate.");
                return;
            }

            if (cursor.getAmount() > 1) {
                cursor.setAmount(cursor.getAmount() - 1);
            } else {
                event.setCursor(null);
            }

            ItemBuilder.of(currentItem, true).durability(0);
            ((Player) event.getWhoClicked()).updateInventory();
        }
    }
}
