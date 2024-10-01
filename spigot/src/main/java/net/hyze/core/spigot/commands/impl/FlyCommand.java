package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.hyze.core.shared.commands.GroupCommandRestrictable;

public class FlyCommand extends CustomCommand implements GroupCommandRestrictable {

    public FlyCommand() {
        super("fly", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;
        Player targetPlayer = null;

        if (args.length > 0) {

            targetPlayer = Bukkit.getPlayerExact(args[0]);

            if (targetPlayer == null) {
                Message.ERROR.send(sender, String.format("O jogador \"%s\" não está online.", args[0]));
                return;
            }

        }

        if (targetPlayer != null && player != targetPlayer) {
            String string = String.format(player.getAllowFlight() ? "Modo voo de \"%s\" ativo." : "Modo voo de \"%s\" desativado.");
            Message.SUCCESS.send(sender, string);
        }

        player.setAllowFlight(!player.getAllowFlight());
        player.setFlying(player.getAllowFlight());
        Message.SUCCESS.send(sender, player.getAllowFlight() ? "Modo voo ativo." : "Modo voo desativado.");

    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }
}
