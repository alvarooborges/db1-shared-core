package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand extends CustomCommand implements GroupCommandRestrictable {

    public ClearCommand() {
        super("clear", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player target = null;

        if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (!(sender instanceof Player) && args.length == 0) {
            Message.ERROR.send(sender, "Utilize /clear <nick>.");
            return;
        }

        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
        }

        if (target == null) {
            Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(args[0]));
            return;
        }

        if (!user.hasGroup(Group.MANAGER)) {
            target = (Player) sender;
        }

        PlayerUtils.clear(target);

        if (((Player) sender) != target) {
            Message.SUCCESS.send(sender, "Você limpou o inventário de " + target.getName() + "!");
            return;
        }

        Message.SUCCESS.send(sender, "Seu inventário agora está vazio.");
    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }
}
