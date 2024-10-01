package net.hyze.core.spigot.events.chat;

import net.hyze.core.shared.user.User;
import org.bukkit.entity.Player;

public class PlayerLocalChatEvent extends PlayerChatEvent {

    public PlayerLocalChatEvent(Player player, User user, String message) {
        super(player, user, message);
    }
}
