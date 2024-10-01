package net.hyze.core.spigot.applications;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.apps.ServerStatus;
import net.hyze.core.shared.servers.Server;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class SpigotStatus extends ServerStatus {

    @Setter
    @Getter
    private Double recentTps = 0.0;

    public SpigotStatus(String appId, AppType type, Server server, InetSocketAddress address, Long onlineSince, long usageMemory, long freeMemory, double tps, boolean maintenance) {
        super(appId, type, server, address, onlineSince, usageMemory, freeMemory, 0, maintenance);
        this.recentTps = tps;
    }
}
