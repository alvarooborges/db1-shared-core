package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.inventory.CustomPlayerInventory;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand extends CustomCommand implements GroupCommandRestrictable {

    public InvseeCommand() {
        super("invsee", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;

        String targetRaw = args[0];

        Player target = Bukkit.getPlayerExact(targetRaw);

        if (target == player) {
            Message.ERROR.send(sender, "Você não pode usar esse comando em você mesmo.");
            return;
        }

        if (target != null) {
            player.closeInventory();
            CustomPlayerInventory inventory = new CustomPlayerInventory(target, true);
            player.openInventory(inventory.getBukkitInventory());
            return;
        }

        Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(targetRaw));

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
