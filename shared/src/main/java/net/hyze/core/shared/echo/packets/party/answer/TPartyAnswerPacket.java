package net.hyze.core.shared.echo.packets.party.answer;

import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ServerPacket
@ExternalPacket(channel="party")
@NoArgsConstructor
public class TPartyAnswerPacket extends AbstractPartyAnswerPacket implements Respondable<TPartyAnswerPacket.AnswerCallback> {

    @Setter
    @Getter
    private AnswerCallback response;

    public TPartyAnswerPacket(User requester, User target, Integer partyId, Integer targetPartyId, EnumAnswer response) {
        super(requester, target, partyId, targetPartyId, response);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerCallback extends Response {

        @Getter
        private boolean success;

        @Override
        public void write(EchoBufferOutput buffer) {
            buffer.writeBoolean(success);
        }

        @Override
        public void read(EchoBufferInput buffer) {
            this.success = buffer.readBoolean();
        }
    }
}
