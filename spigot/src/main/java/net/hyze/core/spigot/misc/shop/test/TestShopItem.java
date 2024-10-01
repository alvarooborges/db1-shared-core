package net.hyze.core.spigot.misc.shop.test;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.shop.ShopItem;
import net.hyze.core.spigot.misc.shop.module.AbstractModule;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class TestShopItem extends ShopItem {

    public TestShopItem(String name, AbstractModule... modules) {
        super(name, modules);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
            "Lore 1",
            "Lore 2"
        };
    }

    @Override
    public ItemBuilder getIcon() {
        return ItemBuilder.of(Material.DIRT);
    }

    @Override
    public void onClick(Player player, User user, AbstractModule.State state, InventoryClickEvent event) {
        if (state == AbstractModule.State.SUCCESS) {

            ItemStack item = ItemBuilder.of(Material.DIRT).make();

            if (!InventoryUtils.fits(player.getInventory(), item)) {
                Message.ERROR.send(player, "Seu inventário está cheio, operacao cancelada!");
                return;
            }

            Runnable callback = () -> {
                player.getInventory().addItem(item);
            };

            boolean value = false;

            if (modules != null) {
                for (AbstractModule module : modules) {
                    value |= module.transaction(user, player, (target) -> new TestShopInv(user), callback);
                }
            }

            if (!value) {
                callback.run();
            }
        }
    }

}
