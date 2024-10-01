package net.hyze.core.shared.echo.packets.punishments;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PunishmentApplyPacket extends EchoPacket {

    private Integer userId;
    private Integer applierId;
    private String reason;
    private String proof;
    private Long duration;
    private String type;

    @Override
    public void write(EchoBufferOutput buffer) {
        buffer.writeInt(this.userId);
        buffer.writeInt(this.applierId);
        buffer.writeString(this.reason);
        buffer.writeString(this.proof);
        buffer.writeLong(this.duration);
        buffer.writeString(this.type);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.userId = buffer.readInt();
        this.applierId = buffer.readInt();
        this.reason = buffer.readString();
        this.proof = buffer.readString();
        this.duration = buffer.readLong();
        this.type = buffer.readString();
    }

}
