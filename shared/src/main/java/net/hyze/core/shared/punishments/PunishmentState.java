package net.hyze.core.shared.punishments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public enum PunishmentState {

    PENDING(ChatColor.YELLOW),
    ACTIVE(ChatColor.GREEN),
    ENDED(ChatColor.RED);

    @Getter
    private final ChatColor color;

}
