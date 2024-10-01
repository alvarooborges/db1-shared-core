package net.hyze.core.bungee.messages;

import net.hyze.core.shared.messages.MessageFormatter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class Message extends MessageFormatter<CommandSender> {

    public static Message SUCCESS = new Message("&a");
    public static Message ERROR = new Message("&c");
    public static Message INFO = new Message("&e");
    public static Message GOLDEN = new Message("&6");
    public static Message EMPTY = new Message("");

    public Message(String prefix) {
        super(prefix);
    }

    @Override
    public void send(CommandSender sender, String message) {
        sender.sendMessage(TextComponent.fromLegacyText(getMessage(message)));
    }

}
