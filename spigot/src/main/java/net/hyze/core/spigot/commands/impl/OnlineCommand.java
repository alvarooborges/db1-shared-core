package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.NumberUtils;
import net.hyze.core.shared.servers.Server;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.command.CommandSender;

public class OnlineCommand extends CustomCommand implements GroupCommandRestrictable {

    private int online;

    public OnlineCommand() {
        super("online", CommandRestriction.IN_GAME, "list");
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        Server server = CoreProvider.getApp().getServer();
        this.online = CoreProvider.Cache.Redis.USERS_STATUS.provide().fetchUsersByServer(server).size();
        CoreProvider.getApp().getServer();
        Message.SUCCESS.send(sender, String.format("\n&e%s: &a%s jogadores online. \n ", server.getDisplayName(), NumberUtils.format(this.online)));

    }

    @Override
    public Group getGroup() {
        return Group.MODERATOR;
    }
}
