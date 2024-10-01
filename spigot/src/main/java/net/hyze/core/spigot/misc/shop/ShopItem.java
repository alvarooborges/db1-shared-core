package net.hyze.core.spigot.misc.shop;

import com.google.common.collect.Lists;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.shop.module.AbstractModule;
import net.hyze.core.spigot.misc.shop.module.AbstractModule.State;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
public abstract class ShopItem {

    @Setter
    protected String name;

    protected List<AbstractModule> modules = Lists.newLinkedList();

    public ShopItem(String name, AbstractModule... modules) {
        this.name = name;

        Collections.addAll(this.modules, modules);
    }

    public abstract String[] getDescription();

    public abstract ItemBuilder getIcon();

    public ItemBuilder build(User user) {
        ItemBuilder builder = getIcon().clone();

        if (this.getDescription() != null && this.getDescription().length > 0) {
            builder.lore(this.getDescription());
            builder.lore("");
        }

        String[] lore = null;
        String[] defaultLore = null;
        State state = State.NONE;

        for (AbstractModule module : modules) {
            State cached = module.state(user);

            if (cached.ordinal() > state.ordinal()) {
                state = cached;
                lore = module.addLore(user, state);
            }

            String[] weakLore = module.defaultLore(user, state);
            if (weakLore != null) {
                if (defaultLore == null) {
                    defaultLore = weakLore;
                } else {
                    int length = defaultLore.length;
                    defaultLore = Arrays.copyOf(defaultLore, length + weakLore.length);
                    System.arraycopy(weakLore, 0, defaultLore, length, weakLore.length);
                }
            }
        }

        builder.name(state.getColor().toString() + getName());

        if (defaultLore != null && defaultLore.length > 0) {
            builder.lore(defaultLore);
        }

        if (lore != null && lore.length > 0) {
            if (defaultLore != null && defaultLore.length > 0) {
                builder.lore("");
            }

            builder.lore(lore);
        }

        return build0(user, builder, state);
    }

    protected ItemBuilder build0(User user, ItemBuilder itemCustom, State state) {
        return itemCustom;
    }

    public void onClick(Player player, User user, State state, InventoryClickEvent event) {

    }

    public final Consumer<InventoryClickEvent> getConsumer(User user) {
        return (InventoryClickEvent event) -> {
            State state = State.NONE;
            AbstractModule highest = null;

            for (AbstractModule module : this.getModules()) {
                State cached = module.state(user);

                if (cached.ordinal() > state.ordinal()) {
                    state = cached;
                    highest = module;
                }
            }

            Player player = (Player) event.getWhoClicked();

            if (highest != null) {
                highest.onClick(user, player, state);
            }

            this.onClick(player, user, state, event);
        };
    }

}
