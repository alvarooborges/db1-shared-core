package net.hyze.core.spigot.misc.message;

import net.hyze.core.shared.messages.MessageFormatter;
import net.hyze.core.spigot.misc.utils.CustomSound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message extends MessageFormatter<CommandSender> {

    public static Message SUCCESS = new Message("&a") {
        @Override
        public void send(CommandSender sender, String message) {
            super.send(sender, message);
            if (sender instanceof Player) {
                CustomSound.GOOD.play((Player) sender);
            }
        }
    };

    public static Message ERROR = new Message("&c") {
        @Override
        public void send(CommandSender sender, String message) {
            super.send(sender, message);
            if (sender instanceof Player) {
                CustomSound.BAD.play((Player) sender);
            }
        }
    };

    public static Message INFO = new Message("&e");
    public static Message GOLDEN = new Message("&6");
    public static Message EMPTY = new Message("");

    public Message(String prefix) {
        super(prefix);
    }

    @Override
    public void send(CommandSender sender, String message) {
        if (sender != null) {
            sender.sendMessage(getMessage(message));
        }
    }
}
