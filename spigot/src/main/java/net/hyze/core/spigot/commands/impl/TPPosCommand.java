package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPPosCommand extends CustomCommand implements GroupCommandRestrictable {

    public TPPosCommand() {
        super("tppos", CommandRestriction.IN_GAME);

        registerArgument(new Argument("x", ""));
        registerArgument(new Argument("y", ""));
        registerArgument(new Argument("z", ""));
        registerArgument(new NickArgument("player", "", false));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {

        Player target = null;

        if (sender instanceof Player) {
            target = (Player) sender;
        }

        if ((!(sender instanceof Player) && args.length < 4) || args.length < 3) {
            Message.ERROR.send(sender, "Utilize /tppos <x> <y> <z> [player].");
            return;
        }

        if (args.length > 3) {
            target = Bukkit.getPlayerExact(args[3]);
        }

        if (target == null) {
            Message.ERROR.send(sender, "Jogador não encontrado.");
            return;
        }

        double x;
        double y;
        double z;

        try {

            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);

        } catch (Exception e) {
            Message.ERROR.send(sender, "Você informou uma posição inválida.");
            return;
        }

        Location loc = new Location(target.getWorld(), x, y, z);

        if (sender != target) {
            Message.SUCCESS.send(sender, "Jogador " + target.getName() + " teleportado para as coordenadas: " + x + ", " + y + ", " + z + " no mundo '"
                    + loc.getWorld().getName() + "'.");
        }

        if (target.teleport(loc)) {
            Message.SUCCESS.send(target, "Você foi teleportado para as coordenadas: " + x + ", " + y + ", " + z + " no mundo '" + loc.getWorld().getName() + "'.");
        }

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
