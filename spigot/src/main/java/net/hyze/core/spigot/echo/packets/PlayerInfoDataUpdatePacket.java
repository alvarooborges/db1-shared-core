package net.hyze.core.spigot.echo.packets;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.ServerPacket;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ServerPacket
public class PlayerInfoDataUpdatePacket extends EchoPacket {

    private WrappedGameProfile profile;
    private int latency;
    private EnumWrappers.NativeGameMode gameMode;
    private WrappedChatComponent displayName;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUUID(buffer, this.profile.getUUID());
        buffer.writeString(this.profile.getName());
        buffer.writeInt(this.latency);
        PacketSerializeUtil.writeEnum(buffer, this.gameMode);

        buffer.writeString(Optional.ofNullable(this.displayName).map(WrappedChatComponent::getJson).orElse(null));
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.profile = new WrappedGameProfile(
                PacketSerializeUtil.readUUID(buffer),
                buffer.readString()
        );
        this.latency = buffer.readInt();
        this.gameMode = PacketSerializeUtil.readEnum(buffer, EnumWrappers.NativeGameMode.class, EnumWrappers.NativeGameMode.SURVIVAL);

        String json = buffer.readString();

        if (json != null) {
            this.displayName = WrappedChatComponent.fromJson(buffer.readString());
        }
    }

    public static PlayerInfoDataUpdatePacket fromData(PlayerInfoData data) {
        return new PlayerInfoDataUpdatePacket(data.getProfile(), data.getLatency(), data.getGameMode(), data.getDisplayName());
    }
}
