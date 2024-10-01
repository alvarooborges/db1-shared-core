package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.echo.packets.user.UserConnectPacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import org.bukkit.command.CommandSender;

public class TPHereCommand extends CustomCommand implements GroupCommandRestrictable {

    public TPHereCommand() {
        super("tphere", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("nick", "Nick do jogador", true));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        if (targetUser == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!targetUser.isLogged()) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", targetUser.getNick()));
            return;
        }

        TeleportManager.teleport(
                targetUser,
                user,
                UserConnectPacket.Reason.PLUGIN,
                String.format("&aVocê foi teleportado até %s.", targetUser.getNick())
        );

    }

    @Override
    public Group getGroup() {
        return Group.ADMINISTRATOR;
    }
}
