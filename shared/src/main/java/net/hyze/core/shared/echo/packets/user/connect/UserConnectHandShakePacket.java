package net.hyze.core.shared.echo.packets.user.connect;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;

@Getter
@NoArgsConstructor
public class UserConnectHandShakePacket extends EchoPacket {

    private User user;
    private String appId;
    private SerializedLocation location;
    private User targetUser;
    private ConnectReason reason;

    @Setter
    private ConnectConsent proxyConsent = ConnectConsent.PENDING;

    @Setter
    private ConnectConsent targetAppConsent = ConnectConsent.PENDING;

    @Setter
    private ConnectConsent currentAppConsent = ConnectConsent.PENDING;

    public UserConnectHandShakePacket(User user, String appId, ConnectReason reason) {
        this.user = user;
        this.appId = appId;
        this.reason = reason;
    }

    public UserConnectHandShakePacket(User user, String appId, SerializedLocation location, ConnectReason reason) {
        this.user = user;
        this.appId = appId;
        this.location = location;
        this.reason = reason;
    }

    public UserConnectHandShakePacket(User user, String appId, User targetUser, ConnectReason reason) {
        this.user = user;
        this.appId = appId;
        this.targetUser = targetUser;
        this.reason = reason;
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeString(buffer, this.appId);
        PacketSerializeUtil.writeSerializedLocation(buffer, this.location);
        PacketSerializeUtil.writeUser(buffer, this.targetUser);
        PacketSerializeUtil.writeEnum(buffer, this.reason);

        PacketSerializeUtil.writeEnum(buffer, this.proxyConsent);
        PacketSerializeUtil.writeEnum(buffer, this.targetAppConsent);
        PacketSerializeUtil.writeEnum(buffer, this.currentAppConsent);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.appId = PacketSerializeUtil.readString(buffer);
        this.location = PacketSerializeUtil.readSerializedLocation(buffer);
        this.targetUser = PacketSerializeUtil.readUser(buffer);
        this.reason = PacketSerializeUtil.readEnum(buffer, ConnectReason.class);

        this.proxyConsent = PacketSerializeUtil.readEnum(buffer, ConnectConsent.class);
        this.targetAppConsent = PacketSerializeUtil.readEnum(buffer, ConnectConsent.class);
        this.currentAppConsent = PacketSerializeUtil.readEnum(buffer, ConnectConsent.class);
    }
}
