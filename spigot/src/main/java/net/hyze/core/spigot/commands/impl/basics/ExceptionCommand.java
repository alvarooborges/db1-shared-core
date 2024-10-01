package net.hyze.core.spigot.commands.impl.basics;

import lombok.Getter;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import org.bukkit.command.CommandSender;

public class ExceptionCommand extends CustomCommand implements GroupCommandRestrictable {

    @Getter
    private Group group = Group.GAME_MASTER;

    public ExceptionCommand() {
        super("exception", CommandRestriction.IN_GAME);
    }


    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        System.out.println(args[10]);
    }
}
