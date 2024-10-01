package net.hyze.core.spigot.misc.shop.module.currency.prices;

import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.shop.module.AbstractModule;
import net.hyze.core.spigot.misc.shop.module.AbstractModule.State;
import net.hyze.core.spigot.misc.shop.module.currency.AbstractPrice;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

@RequiredArgsConstructor
public class CashPrice extends AbstractPrice {

    private final int price;

    @Override
    public AbstractModule.State state(User user) {        
        if (user.getCash() < this.price) {
            return State.ERROR;
        }

        return State.SUCCESS;
    }

    @Override
    public String format() {
        return ChatColor.GOLD.toString() + this.price + " Cash";
    }

    @Override
    public ItemBuilder buildIcon(User user) {
        ItemBuilder icon = ItemBuilder.of(Material.GOLD_INGOT);

        icon.name((this.state(user) == State.SUCCESS ? "&a" : "&c") + this.getName());

        if (this.state(user) == State.SUCCESS) {
            icon.lore("&7Você gastará " + this.format() + "&7.");
        } else {
            icon.lore("Você não possui Cash")
                    .lore("suficiente para efetuar")
                    .lore("esta compra.");
        }

        return icon;
    }

    @Override
    public String getName() {
        return "Cash";
    }

    @Override
    public boolean transaction(User user) {        
        int cash = user.getRealCash();

        if (cash < this.price) {
            return false;
        }

        return user.decrementCash(this.price);
    }

    @Override
    public boolean needsConfirmation() {
        return true;
    }
}
