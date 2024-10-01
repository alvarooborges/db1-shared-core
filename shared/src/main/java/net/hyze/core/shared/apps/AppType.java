package net.hyze.core.shared.apps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.CoreProvider;

@Getter
@RequiredArgsConstructor
public enum AppType {

    FACTIONS_WORLD(true),
    FACTIONS_SPAWN(true),
    FACTIONS_MINE(true),
    FACTIONS_VIP(true),
    FACTIONS_END(true),
    FACTIONS_NETHER(true),
    FACTIONS_TESTS(true),
    FACTIONS_EXCAVATION(true),
    FACTIONS_WAR(true),
    FACTIONS_LOSTFORTRESS(true),
    FACTIONS_ARENA(true),
    SKYBLOCK_ISLANDS(true),
    SKYBLOCK_HUB(true),
    LOBBY(true),
    PROXY(false),
    GENERIC(false),
    DUNGEON(true),
    MINIGAME(true),
    BUILD(true);

    private final boolean allowProxyRegistry;

    public boolean isCurrent() {
        return CoreProvider.getApp().getType() == this;
    }
}
