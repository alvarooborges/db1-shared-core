package net.hyze.core.spigot.misc.shop.module.currency;

import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.shop.module.AbstractModule.State;
import net.hyze.core.spigot.misc.utils.ItemBuilder;

public abstract class AbstractPrice {

    public abstract State state(User user);

    public abstract String format();

    public abstract ItemBuilder buildIcon(User user);

    public abstract String getName();

    public abstract boolean transaction(User user);

    public boolean needsConfirmation() {
        return false;
    }

}
