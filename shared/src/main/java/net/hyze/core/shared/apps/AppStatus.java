package net.hyze.core.shared.apps;

import lombok.*;
import net.hyze.core.shared.servers.Server;

import java.net.InetSocketAddress;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"appId"})
public class AppStatus {

    @NonNull
    private String appId;

    @NonNull
    private AppType type;

    private Server server;

    @NonNull
    private InetSocketAddress address;

    @NonNull
    private Long onlineSince;

    @NonNull
    @Setter
    private long usageMemory, freeMemory;

    @Setter
    private boolean maintenance;

    public AppStatus(@NonNull String appId, @NonNull AppType type, Server server, @NonNull InetSocketAddress address, @NonNull Long onlineSince) {
        this.appId = appId;
        this.type = type;
        this.server = server;
        this.address = address;
        this.onlineSince = onlineSince;
    }
}
