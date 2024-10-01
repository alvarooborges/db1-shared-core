package net.hyze.core.spigot.misc.tpa.events;

import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class TPAcceptEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Setter
    protected boolean cancelled;

    private final Player player;
    
    private final User requester;

    private final User target;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
