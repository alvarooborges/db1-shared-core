package net.hyze.core.shared.echo.packets.party.promote;

import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.EchoBufferInput;
import net.hyze.core.shared.echo.api.EchoBufferOutput;
import net.hyze.core.shared.echo.api.EchoPacket;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractPartyPromotePacket extends EchoPacket {

    @Getter
    private User leader;

    @Getter
    private Integer partyId;

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.leader);
        buffer.writeInt(partyId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.leader = PacketSerializeUtil.readUser(buffer);
        this.partyId = buffer.readInt();
    }

}
