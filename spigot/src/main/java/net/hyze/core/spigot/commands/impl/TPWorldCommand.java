package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPWorldCommand extends CustomCommand implements GroupCommandRestrictable {

    public TPWorldCommand() {
        super("tpworld", CommandRestriction.IN_GAME, "tpw");

        registerArgument(new NickArgument("mundo", "Nome do mundo a teleportar", true));
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        String worldName = args[0];
        World world  = Bukkit.getWorld(worldName);

        if(world == null) {
            Message.ERROR.send(sender, "Esse mundo não existe!");
            return;
        }

        ((Player) sender).teleport(world.getSpawnLocation());
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }
}
