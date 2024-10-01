package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.shop.test.TestShopInv;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopTest extends CustomCommand implements GroupCommandRestrictable {

    public ShopTest() {
        super("shoptest", CommandRestriction.IN_GAME);
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        Player player = (Player) sender;

        player.openInventory(new TestShopInv(user));
    }
}
