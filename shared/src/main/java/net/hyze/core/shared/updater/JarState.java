package net.hyze.core.shared.updater;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
public enum JarState {

    UPDATED(ChatColor.GREEN), OUT_OF_DATE(ChatColor.YELLOW), OTHER(ChatColor.GRAY);

    @Getter
    private final ChatColor color;

}
