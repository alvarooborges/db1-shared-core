package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ThorCommand extends CustomCommand implements GroupCommandRestrictable {

    public ThorCommand() {
        super("thor", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            Message.ERROR.send(sender, "Use /thor [player].");
            return;
        }

        if (args.length == 0) {
            Player player = (Player) sender;
            Block targetBlock = player.getTargetBlock((Set) null, 150);

            targetBlock.getWorld().strikeLightning(targetBlock.getLocation());

            Message.SUCCESS.send(player, "O bloco que você está olhando foi atingido por um raio.");
        } else {
            Player target = Bukkit.getPlayerExact(args[0]);

            if (target == null) {
                Message.ERROR.send(sender, "Jogador não encontrado.");
                return;
            }

            if (target != sender) {
                Message.SUCCESS.send(sender, "O jogador " + target.getName() + " foi atingido por um raio!");
            }

            target.getWorld().strikeLightning(target.getLocation());
            Message.GOLDEN.send(target, ChatColor.GOLD + "Você foi atingido por um raio!");
        }
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
