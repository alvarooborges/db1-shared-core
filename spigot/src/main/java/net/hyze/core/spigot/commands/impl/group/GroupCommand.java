package net.hyze.core.spigot.commands.impl.group;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.commands.impl.group.subcommands.AddGroupCommand;
import net.hyze.core.spigot.commands.impl.group.subcommands.RemoveGroupCommand;

public class GroupCommand extends CustomCommand implements GroupCommandRestrictable {

    public GroupCommand() {
        super("group", CommandRestriction.CONSOLE_AND_IN_GAME);

        registerSubCommand(new AddGroupCommand());
        registerSubCommand(new RemoveGroupCommand());
    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }
}
