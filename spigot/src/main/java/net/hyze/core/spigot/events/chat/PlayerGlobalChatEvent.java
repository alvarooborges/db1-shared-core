package net.hyze.core.spigot.events.chat;

import net.hyze.core.shared.user.User;
import org.bukkit.entity.Player;

public class PlayerGlobalChatEvent extends PlayerChatEvent {

    public PlayerGlobalChatEvent(Player player, User user, String message) {
        super(player, user, message);
    }
}
