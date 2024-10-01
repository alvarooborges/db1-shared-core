package net.hyze.core.spigot.misc.shop;

import lombok.Getter;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.PaginateInventory;

public class PaginateShopInventory extends PaginateInventory {

    @Getter
    protected User user;

    public PaginateShopInventory(String title, User user) {
        super(title);
        this.user = user;
    }

    public void addItem(ShopItem item) {
        this.addItem(item.build(this.user).make(), item.getConsumer(this.user));
    }
}
