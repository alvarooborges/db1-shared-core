package net.hyze.core.spigot.commands.impl;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.commands.arguments.NickArgument;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.utils.DefaultMessage;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class OpCommand extends CustomCommand implements GroupCommandRestrictable {

    public OpCommand() {
        super("op", CommandRestriction.CONSOLE_AND_IN_GAME);

        registerArgument(new NickArgument("nick", "", true));
    }

    @Override
    public Group getGroup() {
        return Group.GAME_MASTER;
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        User target = CoreProvider.Cache.Local.USERS.provide().get(args[0]);

        if (target == null) {
            Message.ERROR.send(sender, DefaultMessage.PLAYER_NOT_FOUND.format(args[0]));
            return;
        }

        if (!AppType.BUILD.isCurrent() && !target.hasGroup(Group.ADMINISTRATOR)) {
            Message.ERROR.send(sender, "O jogador não pode receber op.");
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(target.getNick());
        player.setOp(true);

        ComponentBuilder message = new ComponentBuilder("\n")
                .color(ChatColor.YELLOW)
                .append(sender.getName())
                .append(" deu op para o jogador ")
                .append(target.getNick())
                .append(" no servidor ")
                .append(CoreProvider.getApp().getId())
                .append(".\n");

        BroadcastMessagePacket packet = BroadcastMessagePacket.builder()
                .groups(Collections.singleton(Group.HELPER))
                .components(message.create())
                .build();

        Command.broadcastCommandMessage(sender, "Opped " + args[0]);
        CoreProvider.Redis.ECHO.provide().publish(packet);
    }
}
