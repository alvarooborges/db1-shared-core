package net.hyze.core.spigot.misc.tpa.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.echo.packets.tpa.TPDenyPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.tpa.TPAManager;
import org.bukkit.command.CommandSender;

public class TPDenyCommand extends CustomCommand {

    public TPDenyCommand() {
        super("tpdeny", CommandRestriction.IN_GAME, "bring");

        registerArgument(new NickArgument("jogador", "jogador que você deseja aceitar o pedido de teleporte"));
    }

    @Override
    public void onCommand(CommandSender sender, User target, String[] args) {
        User requester = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        if (requester == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!TPAManager.hasRequest(requester, target)) {
            Message.ERROR.send(sender, String.format("%s não te enviou um pedido de teleporte.", requester.getNick()));
            return;
        }

        CoreProvider.Redis.ECHO.provide().publish(new TPDenyPacket(requester, target));
        Message.INFO.send(sender, String.format("Você negou o pedido de teleporte de %s.", requester.getNick()));
    }
}
