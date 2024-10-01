package net.hyze.core.spigot.misc.scoreboard.bukkit;

import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.utils.SequencePrefix;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

public class GroupScoreboard extends BaseScoreboard {

    public GroupScoreboard(Player player) {
        super(player);
    }

    public GroupScoreboard() {
        super();
    }

    public void registerTeams() {
        for (Group group : Group.values()) {
            fetchOrCreateTeam(group);
        }
    }

    public void registerUser(String nick, Group group) {
        Team entryTeam = scoreboard.getEntryTeam(nick);

        if (entryTeam != null) {
            entryTeam.removeEntry(nick);
        }

        fetchOrCreateTeam(group).addEntry(nick);
    }

    public void registerNPC(String name) {
        Team team = fetchOrCreateNPCTeam();

        team.addEntry(name);
    }

    protected Team fetchOrCreateNPCTeam() {
        Team team;

        String teamName = "npc";

        if ((team = scoreboard.getTeam(teamName)) == null) {
            team = scoreboard.registerNewTeam(teamName);

            team.setPrefix(MessageUtils.translateColorCodes("&8[NPC] "));
            team.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
        }

        return team;
    }

    protected Team fetchOrCreateTeam(Group group) {
        Team team;

        String teamName = getName(group);

        if ((team = scoreboard.getTeam(teamName)) == null) {
            team = scoreboard.registerNewTeam(teamName);

            team.setPrefix(getPrefix(group));
        }

        return team;
    }

    protected String getPrefix(Group group) {
        if (!group.getTag().isEmpty()) {
            return MessageUtils.translateColorCodes(group.getDisplayTag() + " ");
        } else {
            return MessageUtils.translateColorCodes(group.getColor().toString());
        }
    }

    protected String getName(Group group) {
        int index = -group.getPriority() + Group.GAME_MASTER.getPriority() + 1;

        SequencePrefix sequence = new SequencePrefix();

        String prefix = "zzz";

        for (int i = 0; i < index; i++) {
            prefix = sequence.next();
        }

        return String.format(
                "%s",
                prefix
        );
    }
}
