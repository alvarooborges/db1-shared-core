package net.hyze.core.spigot.misc.shop.module;

import net.hyze.core.shared.user.User;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class AbstractModule {

    public abstract State state(User user);

    public String[] defaultLore(User user, State state) {
        return null;
    }

    public String[] addLore(User user, State state) {
        return null;
    }

    public void onClick(User user, Player player, State state) {
    }

    public boolean transaction(User user, Player player, Function<User, Inventory> mainInventory, Runnable callback) {
        return false;
    }

    @RequiredArgsConstructor
    public static enum State {

        // ordinal = prioridade
        NONE(ChatColor.GOLD),
        SUCCESS(ChatColor.GREEN),
        ERROR(ChatColor.RED),
        AQUIRED(ChatColor.GREEN),
        SELECTED(ChatColor.GREEN);

        @Getter
        private final ChatColor color;

    }

}
