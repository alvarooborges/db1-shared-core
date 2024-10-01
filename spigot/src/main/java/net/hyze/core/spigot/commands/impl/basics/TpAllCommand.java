package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllCommand extends CustomCommand implements GroupCommandRestrictable {

    public TpAllCommand() {
        super("tpall", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = null;

        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (!(sender instanceof Player) && args.length < 0) {
            Message.ERROR.send(sender, "Utilize /tpall [player].");
            return;
        }

        if (args.length >= 1) {
            player = Bukkit.getPlayerExact(args[0]);
        }

        if (player == null) {
            Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(args[0]));
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target != player) {
                if (target.teleport(player)) {
                    Message.SUCCESS.send(target, "Você foi teleportado até " + player.getName() + ".");
                }
            }
        }

        Message.SUCCESS.send(player, "Todos os jogadores foram teleportados até você.");
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
