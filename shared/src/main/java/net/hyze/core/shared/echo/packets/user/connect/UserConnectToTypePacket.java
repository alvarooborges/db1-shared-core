package net.hyze.core.shared.echo.packets.user.connect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserConnectToTypePacket extends EchoPacket {

    private User user;
    private Server server;
    private AppType type;
    private ConnectReason reason = ConnectReason.PLUGIN;

    public UserConnectToTypePacket(User user, AppType type, ConnectReason reason) {
        this(user, null, type, reason);
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeEnum(buffer, this.server);
        PacketSerializeUtil.writeEnum(buffer, this.type);
        PacketSerializeUtil.writeEnum(buffer, this.reason);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.server = PacketSerializeUtil.readEnum(buffer, Server.class);
        this.type = PacketSerializeUtil.readEnum(buffer, AppType.class);
        this.reason = PacketSerializeUtil.readEnum(buffer, ConnectReason.class);
    }
}
