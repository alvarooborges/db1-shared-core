package net.hyze.core.spigot.misc.tags.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;

import java.util.Collection;
import java.util.List;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket {

    public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;

    public WrapperPlayServerScoreboardTeam() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerScoreboardTeam(PacketContainer packet) {
        super(packet, TYPE);
    }

    public static class Mode extends IntEnum {

        public static int TEAM_CREATED = 0;
        public static int TEAM_REMOVED = 1;
        public static int TEAM_UPDATED = 2;
        public static int PLAYERS_ADDED = 3;
        public static int PLAYERS_REMOVED = 4;

        private static Mode INSTANCE = new Mode();

        public static Mode getInstance() {
            return INSTANCE;
        }
    }

    public String getName() {
        return handle.getStrings().read(0);
    }

    public void setName(String value) {
        handle.getStrings().write(0, value);
    }

    public String getDisplayName() {
        return handle.getStrings().read(1);
    }

    public void setDisplayName(String value) {
        handle.getStrings().write(1, value);
    }

    public String getPrefix() {
        return handle.getStrings().read(2);
    }

    public void setPrefix(String value) {
        handle.getStrings().write(2, value);
    }

    public String getSuffix() {
        return handle.getStrings().read(3);
    }

    public void setSuffix(String value) {
        handle.getStrings().write(3, value);
    }

    public String getNameTagVisibility() {
        return handle.getStrings().read(4);
    }

    public void setNameTagVisibility(String value) {
        handle.getStrings().write(4, value);
    }

    public int getColor() {
        return handle.getIntegers().read(0);
    }

    public void setColor(int value) {
        handle.getIntegers().write(0, value);
    }

    public String getCollisionRule() {
        return handle.getStrings().read(5);
    }

    public void setCollisionRule(String value) {
        handle.getStrings().write(5, value);
    }

    public Collection<String> getPlayers() {
        return (Collection<String>) handle.getSpecificModifier(Collection.class).read(0);
    }

    public void setPlayers(List<String> value) {
        handle.getSpecificModifier(Collection.class).write(0, value);
    }

    public int getMode() {
        return handle.getIntegers().read(1);
    }

    public void setMode(int value) {
        handle.getIntegers().write(1, value);
    }

    public int getPackOptionData() {
        return handle.getIntegers().read(2);
    }

    public void setPackOptionData(int value) {
        handle.getIntegers().write(2, value);
    }
}
