package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.utils.HeadTexture;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeadCommand extends CustomCommand implements GroupCommandRestrictable {

    public HeadCommand() {
        super("head", CommandRestriction.IN_GAME);
        
        registerArgument(new Argument("key", "um código muito doido"));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Player player = (Player) sender;

        player.getInventory().addItem(
                new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .skullUrl(HeadTexture.TEXTURE_API_URL + args[0])
                .make()
        );

    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

}
