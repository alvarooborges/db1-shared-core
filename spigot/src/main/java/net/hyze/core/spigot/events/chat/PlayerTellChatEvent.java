package net.hyze.core.spigot.events.chat;

import net.hyze.core.shared.user.User;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerTellChatEvent extends PlayerChatEvent {

    private final User target;

    public PlayerTellChatEvent(Player player, User user, User target, String message) {
        super(player, user, message);
        this.target = target;
    }
}
