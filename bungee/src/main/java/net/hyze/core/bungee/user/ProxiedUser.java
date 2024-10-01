package net.hyze.core.bungee.user;

import net.hyze.core.shared.user.User;
import java.net.InetSocketAddress;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ProxiedUser {

    @Setter
    private User user;

    @Setter
    private Integer sessionId;

    private final String ip;
    private final int version;

    private final Date joinedAt;

}
