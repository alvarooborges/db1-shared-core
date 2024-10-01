package net.hyze.core.spigot.misc.scoreboard.protocol.api;

import net.hyze.core.spigot.CoreSpigotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PersonalBoard extends Board implements Listener {

    private final Player owner;
    private boolean registered = false;

    public PersonalBoard(String displayName, Player owner) {
        super(displayName);
        this.owner = owner;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer() == owner) {
            remove();
        }
    }

    @Override
    public void create() {
        super.create();

        Bukkit.getPluginManager().registerEvents(this, CoreSpigotPlugin.getInstance());

        send(owner);

        registered = true;
    }

    @Override
    public void remove() {
        super.remove();

        if (registered) {
            HandlerList.unregisterAll(this);
        }

        registered = false;
    }

    public Player getOwner() {
        return owner;
    }
}
