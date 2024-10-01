package net.hyze.core.shared.echo.packets.user;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;

@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@Deprecated
public class UserConnectPacket extends EchoPacket {

    @NonNull
    private User user;

    @Nullable
    private App toApp;

    @Nullable
    private User toUser;

    @Nullable
    private SerializedLocation toLocation;

    @NonNull
    private Reason reason;

    private String joinMessage;

    @NonNull
    @Setter
    private Consent proxyConsent = Consent.PENDING;

    @NonNull
    @Setter
    private Consent targetAppConsent = Consent.PENDING;

    @NonNull
    @Setter
    private Consent currentAppConsent = Consent.PENDING;

    @Setter
    private JsonObject payload;

    @Setter
    private boolean allowSplit = true;

    public UserConnectPacket(User user, App toApp, User toUser, SerializedLocation toLocation, Reason reason, String joinMessage) {
        this.user = user;
        this.toApp = toApp;
        this.toUser = toUser;
        this.toLocation = toLocation;
        this.reason = reason;
        this.joinMessage = joinMessage;
    }

    public UserConnectPacket(User user, App app, Reason reason) {
        this(user, app, reason, null);
    }

    public UserConnectPacket(User user, User target, Reason reason) {
        this(user, target, reason, null);
    }

    public UserConnectPacket(User user, SerializedLocation location, Reason reason) {
        this(user, location, reason, null);
    }

    public UserConnectPacket(User user, App app, Reason reason, String joinMessage) {
        this(user, app, null, null, reason, joinMessage);
    }

    public UserConnectPacket(User user, User target, Reason reason, String joinMessage) {
        this(user, null, target, null, reason, joinMessage);
    }

    public UserConnectPacket(User user, SerializedLocation location, Reason reason, String joinMessage) {
        this(user, null, null, location, reason, joinMessage);
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
        PacketSerializeUtil.writeUser(buffer, this.toUser);
        PacketSerializeUtil.writeApp(buffer, this.toApp);
        PacketSerializeUtil.writeSerializedLocation(buffer, this.toLocation);
        PacketSerializeUtil.writeEnum(buffer, this.reason);
        PacketSerializeUtil.writeString(buffer, this.joinMessage);
        PacketSerializeUtil.writeEnum(buffer, this.proxyConsent);
        PacketSerializeUtil.writeEnum(buffer, this.targetAppConsent);
        PacketSerializeUtil.writeEnum(buffer, this.currentAppConsent);
        PacketSerializeUtil.writeJsonObject(buffer, this.payload);
        buffer.writeBoolean(this.allowSplit);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
        this.toUser = PacketSerializeUtil.readUser(buffer);
        this.toApp = PacketSerializeUtil.readApp(buffer);
        this.toLocation = PacketSerializeUtil.readSerializedLocation(buffer);
        this.reason = PacketSerializeUtil.readEnum(buffer, Reason.class);
        this.joinMessage = PacketSerializeUtil.readString(buffer);
        this.proxyConsent = PacketSerializeUtil.readEnum(buffer, Consent.class, Consent.PENDING);
        this.targetAppConsent = PacketSerializeUtil.readEnum(buffer, Consent.class, Consent.PENDING);
        this.currentAppConsent = PacketSerializeUtil.readEnum(buffer, Consent.class, Consent.PENDING);
        this.payload = PacketSerializeUtil.readJsonObject(buffer);
        this.allowSplit = buffer.readBoolean();
    }

    public App getTargetApp() {
        App app;

        if (this.toUser != null) {
            app = CoreProvider.Cache.Local.USERS_STATUS.provide().getBukkitApp(this.toUser.getNick());
        } else if (this.toLocation != null) {
            app = this.toLocation.getApp();
        } else {
            app = this.toApp;
        }

        return app;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static enum Reason {
        JOIN, WARP, TPA(false), HOME, PLUGIN, RESPAWN, RECONNECT;

        private boolean allowSplit = true;
    }

    public static enum Consent {
        ALLOWED,
        DENIED,
        PENDING;
    }
}
