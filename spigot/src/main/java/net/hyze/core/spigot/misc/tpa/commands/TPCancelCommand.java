package net.hyze.core.spigot.misc.tpa.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.echo.packets.tpa.TPCancelPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.tpa.TPAManager;
import org.bukkit.command.CommandSender;

public class TPCancelCommand extends CustomCommand {

    public TPCancelCommand() {
        super("tpcancel", CommandRestriction.IN_GAME);

        registerArgument(new NickArgument("jogador", "jogador que você deseja aceitar o pedido de teleporte"));
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        User target = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        if (target == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!TPAManager.hasRequest(requester, target)) {
            Message.ERROR.send(sender, String.format("Você não enviou um pedido de teleporte para %s.", target.getNick()));
            return;
        }

        CoreProvider.Redis.ECHO.provide().publish(new TPCancelPacket(requester, target));
        Message.INFO.send(sender, String.format("Pedido de teleporte para %s cancelado.", target.getNick()));
    }
}
