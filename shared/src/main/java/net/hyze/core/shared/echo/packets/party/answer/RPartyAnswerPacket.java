package net.hyze.core.shared.echo.packets.party.answer;

import net.hyze.core.shared.echo.api.ServerPacket;
import net.hyze.core.shared.user.User;
import lombok.NoArgsConstructor;

@ServerPacket
@NoArgsConstructor
public class RPartyAnswerPacket extends AbstractPartyAnswerPacket {

    public RPartyAnswerPacket(User requester, User target, Integer partyId, Integer targetPartyId, EnumAnswer response) {
        super(requester, target, partyId, targetPartyId, response);
    }

}
