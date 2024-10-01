package net.hyze.core.spigot.misc.scoreboard.bukkit;

import net.hyze.core.shared.CoreConstants;

public interface ScoreboardMarkable {

    default String mark() {
        return String.format("&e%s", CoreConstants.Infos.SITE_DOMAIN);
    }
}
