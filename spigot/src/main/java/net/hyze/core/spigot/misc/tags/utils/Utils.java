package net.hyze.core.spigot.misc.tags.utils;

public class Utils {

    public static String parseName(String input) {
        if (input.length() > 15) {
            return input.substring(0, 15);
        }
        return input;
    }

    public static String parse(String input) {
        if (input.length() > 16) {
            return input.substring(0, 16);
        }
        return input;
    }
}
