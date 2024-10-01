package net.hyze.core.shared.echo.packets.party.invite;

import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.party.Party;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class AbstractPartyInvitePacket extends EchoPacket {

    @Getter
    private User requester;

    @Getter
    private User target;

    @Getter
    private Integer partyId;

    public AbstractPartyInvitePacket(User requester, User target, Integer partyId) {
        this.requester = requester;
        this.target = target;
        this.partyId = partyId;
    }

    @Override
    public void write(EchoBufferOutput buffer) {
        PacketSerializeUtil.writeUser(buffer, this.requester);
        PacketSerializeUtil.writeUser(buffer, this.target);
        buffer.writeInt(this.partyId);
    }

    @Override
    public void read(EchoBufferInput buffer) {
        this.requester = PacketSerializeUtil.readUser(buffer);
        this.target = PacketSerializeUtil.readUser(buffer);
        this.partyId = buffer.readInt();
    }

}

