package net.hyze.core.spigot.commands.impl.basics;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand extends CustomCommand implements GroupCommandRestrictable {

    public HatCommand() {
        super("hat", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User requester, String[] args) {
        Player player = (Player) sender;

        ItemStack item = player.getItemInHand();

        if (item != null && !item.getType().equals(Material.AIR)) {
            if (item.getType().getMaxDurability() > 0) {
                Message.ERROR.send(sender, "Não é possível por este item na cabeça.");
                return;
            }
            ItemStack inHead = player.getInventory().getHelmet();
            player.getInventory().setHelmet(item);
            player.setItemInHand(inHead);

            Message.SUCCESS.send(sender, "Você colocou um chapéu!");
        } else {
            Message.ERROR.send(sender, "Você não está segurando nenhum item.");
        }
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
