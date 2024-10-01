package net.hyze.core.spigot.misc.tpa.events;

import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class TPAEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlers = new HandlerList();

    @Setter
    protected boolean cancelled;

    private final User requester;
    private final User targer;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
