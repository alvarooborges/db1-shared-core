package net.hyze.core.shared.echo.api;

import net.hyze.core.shared.apps.App;
import java.util.UUID;

import net.hyze.core.shared.servers.Server;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class EchoPacketHeader implements IByteSerializable {

    private App sender;
    private App target;

    private Server senderServer;

    private UUID UUID;

    private UUID responseUUID;

    @Setter
    private String channel;

    protected EchoPacketHeader(App sender, Server senderServer, UUID UUID) {
        this.sender = sender;
        this.senderServer = senderServer;
        this.UUID = UUID;
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeApp(buffer, this.sender);
        PacketSerializeUtil.writeApp(buffer, this.target);
        PacketSerializeUtil.writeServer(buffer, this.senderServer);
        PacketSerializeUtil.writeUUID(buffer, this.UUID);
        PacketSerializeUtil.writeUUID(buffer, this.responseUUID);
        PacketSerializeUtil.writeString(buffer, channel);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.sender = PacketSerializeUtil.readApp(buffer);
        this.target = PacketSerializeUtil.readApp(buffer);
        this.senderServer = PacketSerializeUtil.readServer(buffer);
        this.UUID = PacketSerializeUtil.readUUID(buffer);
        this.responseUUID = PacketSerializeUtil.readUUID(buffer);
        this.channel = PacketSerializeUtil.readString(buffer);
    }

    public boolean hasTarget() {
        return this.target != null;
    }
}
