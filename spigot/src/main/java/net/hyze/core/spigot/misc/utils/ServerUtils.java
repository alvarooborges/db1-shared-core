package net.hyze.core.spigot.misc.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Server;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerUtils {

    public static boolean isDay() {
        Server server = Bukkit.getServer();
        long time = server.getWorld("world").getTime();

        return time < 12300 || time > 23850;
    }
}
