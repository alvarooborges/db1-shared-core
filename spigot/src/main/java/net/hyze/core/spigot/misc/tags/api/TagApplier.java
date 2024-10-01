package net.hyze.core.spigot.misc.tags.api;

import org.bukkit.entity.Player;

public class TagApplier {

    public static void applyTag(Player player, String prefix) {
        applyTag(player, prefix, "");
    }

    public static void applyTag(Player player, String prefix, String suffix) {
        applyTag(player, player.getName(), prefix, suffix);
    }

    public static void applyTag(Player player, String team, String prefix, String suffix) {
        TagManager.setTag(player, team, prefix, suffix);
    }
}
