package net.hyze.core.spigot.misc.utils;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.packetwrapper.WrapperPlayServerChat;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class ActionBarMessage {

    private String text;

    @Getter
    private final Spigot spigot;

    public ActionBarMessage() {
        this.spigot = new Spigot();
    }

    public ActionBarMessage text(String text) {
        this.text = ChatColor.translateAlternateColorCodes('&', text);
        return this;
    }

    public void send(Player... players) {
        this.spigot.send(players);
    }

    public class Spigot {

        private BukkitTask task;
        private int stayCounter;

        public Spigot send(Player... players) {

            WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromText(text);
            WrapperPlayServerChat wrapperPlayServerChat = new WrapperPlayServerChat();
            wrapperPlayServerChat.setMessage(wrappedChatComponent);
            wrapperPlayServerChat.setPosition((byte) 2);

            for (Player player : players) {
                wrapperPlayServerChat.sendPacket(player);
            }

            return this;
        }

        public Spigot sendAndStay(JavaPlugin plugin, int ticks, Player... players) {

            task = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {

                    stayCounter++;
                    send(players);

                    if (stayCounter >= ticks) {
                        task.cancel();
                    }

                }
            }, 0L, 1L);

            return this;
        }

        public Spigot cancelStay() {
            task.cancel();
            return this;
        }

    }

}
