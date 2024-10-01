package net.hyze.core.shared.reconnections;

import net.hyze.core.shared.apps.AppStatus;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class Reconnection {

    @Getter
    private final Server server;

    @Getter
    private final User user;

    @Getter
    private final AppStatus status;

}
