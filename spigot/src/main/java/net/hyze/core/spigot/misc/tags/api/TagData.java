package net.hyze.core.spigot.misc.tags.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.hyze.core.spigot.misc.tags.packetwrapper.WrapperPlayServerScoreboardTeam;
import net.hyze.core.spigot.misc.tags.utils.Utils;

import java.util.Collection;

public class TagData {

    private TagPacket packet;

    public TagData(String name, String prefix, String suffix) {
        this(name, TagVisibility.ALWAYS, prefix, suffix);
    }

    public TagData(String name, TagVisibility tagVisibility, String prefix, String suffix) {
        packet = new TagPacket(Utils.parseName(name), WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED, tagVisibility);

        this.packet.unsafe().setPrefix(Utils.parse(prefix));
        this.packet.unsafe().setSuffix(Utils.parse(suffix));

        packet.insertData(this);
    }

    public TagPacket getPacket() {
        return packet;
    }

    public String getName() {
        return getPacket().getName();
    }

    public boolean hasPlayer(Player player) {
        return getPacket().hasPlayer(player);
    }

    public void addPlayer(Player player) {
        getPacket().addPlayer(player, this);
    }

    public void removePlayer(Player player) {
        getPacket().removePlayer(player, this);

        if (getPlayers().isEmpty()) {
            TagManager.removeTeam(this);
        }
    }

    public Collection<Player> getPlayers() {
        return getPacket().getPlayers();
    }

    public String getPrefix() {
        return packet.unsafe().getPrefix();
    }

    public String getSuffix() {
        return packet.unsafe().getSuffix();
    }

    public void destroy() {
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    public void destroy(Player player) {
        TagPacket packet = new TagPacket(getName(), WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED);
        packet.insertData(this);
        packet.send(player);
    }
}
