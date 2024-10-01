package net.hyze.core.shared.apps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.servers.Server;

import java.net.InetSocketAddress;

@NoArgsConstructor
public class ServerStatus extends AppStatus {

    @Setter
    @Getter
    private Integer onlineCount = 0;

    @Setter
    @Getter
    private boolean restarting = false;

    public ServerStatus(String appId, AppType type, Server server, InetSocketAddress address, Long onlineSince, long usageMemory, long freeMemory, int online, boolean maintenance) {
        super(appId, type, server, address, onlineSince, usageMemory, freeMemory, maintenance);
        this.onlineCount = online;
    }
}
