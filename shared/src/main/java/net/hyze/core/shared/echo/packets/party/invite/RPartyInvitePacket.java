package net.hyze.core.shared.echo.packets.party.invite;

import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ServerPacket
@NoArgsConstructor
public class RPartyInvitePacket extends AbstractPartyInvitePacket {

    public RPartyInvitePacket(User requester, User target, Integer partyId) {
        super(requester, target, partyId);
    }
}
