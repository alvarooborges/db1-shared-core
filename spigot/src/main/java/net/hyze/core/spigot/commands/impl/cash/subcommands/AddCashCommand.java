package net.hyze.core.spigot.commands.impl.cash.subcommands;

import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.echo.packets.SendDiscordLogPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.DefaultMessage;
import org.bukkit.command.CommandSender;

public class AddCashCommand extends CustomCommand {

    public AddCashCommand() {
        super("add");

        registerArgument(new Argument("nick", "Nome do jogador."));
        registerArgument(new Argument("valor", "Quantidade de cash."));
        
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        String targetName = args[0];
        User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetName);

        if (targetUser == null) {
            DefaultMessage.PLAYER_NOT_FOUND.send(sender, targetName);
            return;
        }

        Integer amount = Ints.tryParse(args[1]);

        if (amount == null) {
            DefaultMessage.INVALID_NUMBER.send(sender, args[1]);
            return;
        }

        if (amount < 1) {
            DefaultMessage.NEGATIVE_NUMBER.send(sender, null);
            return;
        }


        targetUser.incrementCash(amount);
        Message.SUCCESS.send(sender, String.format("Você adicionou &f%s Cash &ano saldo de &f%s&a.", amount, targetUser.getNick()));

        CoreProvider.Redis.ECHO.provide().publish(new SendDiscordLogPacket(
                String.format(
                        "**%s** adicionou **%s** de cash para %s.",
                        sender.getName(),
                        amount,
                        targetUser.getNick()
                ),
                "game-log"
        ));
    }
}
