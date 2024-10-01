package net.hyze.core.spigot.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerStopEvent;

import java.util.Map;

public class ServerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLowest(ServerStopEvent event) {
        /**
         * Definindo servidor como stopping
         */
        CoreSpigotConstants.STOPPING = true;

        /**
         * Fechando inventário de todos os jogadores
         */
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.closeInventory();
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(ServerStopEvent event) {
        boolean empty = Bukkit.getOnlinePlayers().isEmpty();

        Map<String, AppStatus> lobbies = CoreProvider.Cache.Redis.LOBBIES.provide().fetchAll();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(ChatColor.RED + "Reiniciando servidor.");
        });

        if (!empty) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
