package net.hyze.core.spigot.misc.shop;

import lombok.Getter;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.inventory.CustomInventory;
import net.hyze.core.spigot.inventory.ICustomInventory;

public class ShopInventory extends CustomInventory {

    @Getter
    protected User user;

    public ShopInventory(int size, String title, User user) {
        super(size, title);
        this.user = user;
    }

    public ICustomInventory getRawInventory() {
        return this;
    }

    public void setItem(int index, ShopItem item) {
        this.setItem(index, item.build(this.user).make(), item.getConsumer(this.user));
    }
}
