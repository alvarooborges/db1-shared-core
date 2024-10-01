package net.hyze.core.shared.echo.packets.party.disband;

import net.hyze.core.shared.echo.api.ExternalPacket;
import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.user.User;
import lombok.NoArgsConstructor;

@ServerPacket
@ExternalPacket(channel="party")
@NoArgsConstructor
public class TPartyDisbandPacket extends AbstractPartyDisbandPacket {

    public TPartyDisbandPacket(User leader, Integer partyId) {
        super(leader, partyId);
    }
}
