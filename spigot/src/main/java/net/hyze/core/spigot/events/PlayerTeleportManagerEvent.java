package net.hyze.core.spigot.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerTeleportManagerEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
