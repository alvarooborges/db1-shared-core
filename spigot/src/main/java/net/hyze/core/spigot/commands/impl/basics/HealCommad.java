package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class HealCommad extends CustomCommand implements GroupCommandRestrictable {

    public HealCommad() {
        super("heal", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player target = null;

        if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (!(sender instanceof Player) && args.length == 0) {
            Message.ERROR.send(sender, "Utilize /heal [player].");
            return;
        }

        if (args.length >= 1) {
            target = Bukkit.getPlayerExact(args[0]);
        }

        if (target == null) {
            Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(args[0]));
            return;
        }

        if (target.isDead()) {
            Message.ERROR.send(sender, "Este jogador está morto.");
            return;
        }

        if (target != sender) {
            Message.SUCCESS.send(sender, "O jogador " + target.getName() + " foi curado.");
        }

        PlayerUtils.healPlayer(target);
        target.setExhaustion(0F);
        target.setFireTicks(0);
        target.setFallDistance(0F);

        for (PotionEffect pE : target.getActivePotionEffects()) {
            if (target.hasPotionEffect(pE.getType())) {
                target.removePotionEffect(pE.getType());
            }
        }

        Message.SUCCESS.send(target, "Você foi curado.");
    }

    @Override
    public Group getGroup() {
        return Group.MANAGER;
    }

}
