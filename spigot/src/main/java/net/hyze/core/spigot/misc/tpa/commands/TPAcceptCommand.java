package net.hyze.core.spigot.misc.tpa.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.App;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.echo.packets.tpa.TPAcceptPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.tpa.TPAManager;
import net.hyze.core.spigot.misc.tpa.events.TPAcceptEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAcceptCommand extends CustomCommand {

    public TPAcceptCommand() {
        super("tpaccept", CommandRestriction.IN_GAME, "bring");

        registerArgument(new NickArgument("jogador", "jogador que você deseja aceitar o pedido de teleporte"));
    }

    @Override
    public void onCommand(CommandSender sender, User target, String[] args) {
        User requester = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        if (requester == null) {
            Message.ERROR.send(sender, String.format("O jogador %s não foi encontrado.", args[0]));
            return;
        }

        if (!requester.isLogged()) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", requester.getNick()));
            return;
        }

        if (!TPAManager.hasRequest(requester, target)) {
            Message.ERROR.send(sender, "Você não possui um pedido de teleporte deste jogador.");
            return;
        }

        App requesterApp = CoreProvider.Cache.Redis.USERS_STATUS.provide().getBukkitApp(requester.getNick());
        App targetApp = CoreProvider.Cache.Redis.USERS_STATUS.provide().getBukkitApp(target.getNick());

        if (!targetApp.isSameServer(requesterApp)) {
            Message.ERROR.send(sender, String.format("O jogador %s não está online.", requester.getNick()));
            return;
        }

        TPAcceptEvent event = new TPAcceptEvent((Player) sender, requester, target);

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        Message.SUCCESS.send(sender, String.format("&aVocê aceitou o pedido de teleporte de %s.", requester.getNick()));
        CoreProvider.Redis.ECHO.provide().publish(new TPAcceptPacket(requester, target));
    }
}
