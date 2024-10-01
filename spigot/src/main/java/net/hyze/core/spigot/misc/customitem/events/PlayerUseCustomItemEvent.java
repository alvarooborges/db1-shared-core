package net.hyze.core.spigot.misc.customitem.events;

import net.hyze.core.spigot.misc.customitem.CustomItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class PlayerUseCustomItemEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Getter
    private final Player player;

    @Getter
    private final Event triggerEvent;

    @Getter
    private final CustomItem item;

    @Getter
    @Setter
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
