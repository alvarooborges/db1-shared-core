package net.hyze.core.spigot.echo.packets;

import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.spigot.echo.packets.UserLocationRequest.UserLocationResponse;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.echo.api.ServerPacket;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.hyze.core.shared.echo.api.Respondable;
import net.hyze.core.shared.echo.api.Response;

@ServerPacket
@NoArgsConstructor
@RequiredArgsConstructor
public class UserLocationRequest extends EchoPacket implements Respondable<UserLocationResponse> {

    @Getter
    @Setter
    private UserLocationResponse response;

    @NonNull
    @Getter
    private User user;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.user);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.user = PacketSerializeUtil.readUser(buffer);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLocationResponse extends Response {

        @Getter
        private SerializedLocation location;

        @Override
        public void write(EchoBufferOutput buffer) {
            PacketSerializeUtil.writeSerializedLocation(buffer, this.location);
        }

        @Override
        public void read(EchoBufferInput buffer) {
            this.location = PacketSerializeUtil.readSerializedLocation(buffer);
        }
    }
}
