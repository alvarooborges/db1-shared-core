package net.hyze.core.shared.echo.packets.dungeon.state;

import net.hyze.core.shared.echo.api.*;
import net.hyze.core.shared.echo.packets.party.disband.AbstractPartyDisbandPacket;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@ServerPacket
@ExternalPacket(channel="dungeon")
@RequiredArgsConstructor
public class DungeonStatePacket extends EchoPacket {

    private final String dungeonId;

    private final EnumState state;

    @Override
    public void write(EchoBufferOutput buffer) {

    }

    @Override
    public void read(EchoBufferInput buffer) {

    }

    public static enum EnumState {

        EMPTY, POPULATED

    }
}
