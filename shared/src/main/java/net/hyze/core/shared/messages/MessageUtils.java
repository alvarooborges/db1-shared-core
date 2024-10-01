package net.hyze.core.shared.messages;

import java.util.Arrays;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;

public class MessageUtils {

    public static String translateFormat(String format, Object... args) {
        return MessageUtils.translateColorCodes(String.format(
                format, args
        ));
    }

    public static String translateColorCodes(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String[] translateColorCodes(String... messages) {
        return Arrays.stream(messages).map(MessageUtils::translateColorCodes).toArray(String[]::new);
    }

    public static String stripColor(String message, ChatColor... colors) {

        if (colors.length < 1) {
            colors = ChatColor.values();
        }

        String codes = new String();

        for (ChatColor color : colors) {
            codes += color.toString().toCharArray()[1];
        }

        return Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[" + codes.toUpperCase() + "]")
                .matcher(message)
                .replaceAll("");
    }
}
