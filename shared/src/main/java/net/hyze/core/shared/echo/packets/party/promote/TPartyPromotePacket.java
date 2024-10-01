package net.hyze.core.shared.echo.packets.party.promote;

import net.hyze.core.shared.echo.api.ExternalPacket;
import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.user.User;
import lombok.NoArgsConstructor;

@ServerPacket
@ExternalPacket(channel="party")
@NoArgsConstructor
public class TPartyPromotePacket extends AbstractPartyPromotePacket {

    public TPartyPromotePacket(User leader, Integer partyId) {
        super(leader, partyId);
    }
}
