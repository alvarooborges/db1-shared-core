package net.hyze.core.spigot.misc.shop.test;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.shop.ShopInventory;
import net.hyze.core.spigot.misc.shop.module.currency.CurrencyModule;
import net.hyze.core.spigot.misc.shop.module.currency.prices.CashPrice;

public class TestShopInv extends ShopInventory {

    public TestShopInv(User user) {
        super(6 * 9, "Shop Test", user);

        setItem(1, new TestShopItem("Item 1", new CurrencyModule(
                new CashPrice(10)
        )));
    }

}
