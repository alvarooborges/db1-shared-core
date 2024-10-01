package net.hyze.core.shared.echo.packets.party.leave;

import net.hyze.core.shared.echo.api.ExternalPacket;
import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.user.User;
import lombok.NoArgsConstructor;

@ServerPacket
@ExternalPacket(channel="party")
@NoArgsConstructor
public class TPartyLeavePacket extends AbstractPartyLeavePacket {

    public TPartyLeavePacket(User leader, User target, Integer partyId, LeaveReason reason) {
        super(leader, target, partyId, reason);
    }
}
