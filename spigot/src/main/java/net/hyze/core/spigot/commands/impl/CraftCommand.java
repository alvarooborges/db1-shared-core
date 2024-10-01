package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    public final Group group;

    public CraftCommand(Group group) {
        super("craft", CommandRestriction.IN_GAME);

        this.group = group;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        player.openWorkbench(player.getLocation(), true);
    }
}
