package net.hyze.core.shared.echo.packets.party.invite;

import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.echo.api.PacketSerializeUtil;
import net.hyze.core.shared.user.User;
import lombok.*;

@ServerPacket
@ExternalPacket(channel="party")
@NoArgsConstructor
public class TPartyInvitePacket extends AbstractPartyInvitePacket implements Respondable<TPartyInvitePacket.InviteCallback> {

    @Setter
    @Getter
    private InviteCallback response;

    public TPartyInvitePacket(User requester, User target, Integer partyId) {
        super(requester, target, partyId);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class InviteCallback extends Response {

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
