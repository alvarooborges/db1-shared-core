package net.hyze.core.spigot.misc.modreq.commands;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.echo.packets.BroadcastMessagePacket;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.misc.cooldowns.redis.UserRedisCooldowns;
import net.hyze.core.shared.misc.utils.TimeCode;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.modreq.ModreqManager;
import net.hyze.core.spigot.misc.modreq.echo.packets.ModreqRequestEchoPacket;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class ModreqCommand extends CustomCommand {

    private static String COOLDOWN_KEY = "modreq_cmd";

    public ModreqCommand() {
        super("modreq", CommandRestriction.IN_GAME);
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {
        if (!UserRedisCooldowns.hasEnded(user, COOLDOWN_KEY)) {
            long millisLeft = UserRedisCooldowns.getMillisLeft(user, COOLDOWN_KEY);
            String formattedTimeLeft = TimeCode.getFormattedTimeLeft(millisLeft);

            Message.ERROR.send(sender, String.format(
                    "Aguarde %s para usar o /modreq novamente.",
                    formattedTimeLeft
            ));
            return;
        }

        UserRedisCooldowns.start(user, COOLDOWN_KEY, ModreqManager.DELAY, TimeUnit.MILLISECONDS);
        CoreProvider.Redis.ECHO.provide().publish(new ModreqRequestEchoPacket(user));

        BaseComponent[] components = new ComponentBuilder("\n")
                .color(ChatColor.YELLOW)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/modreqtp " + user.getNick()))
                .append("O jogador " + user.getNick() + " solicitou um staff.")
                .append("\n")
                .append("Clique para ir até ele.")
                .append("\n")
                .create();

        BroadcastMessagePacket packet = BroadcastMessagePacket.builder()
                .groups(Collections.singleton(Group.MODERATOR))
                .server(CoreProvider.getApp().getServer())
                .components(components)
                .build();

        CoreProvider.Redis.ECHO.provide().publish(packet);

        Message.SUCCESS.send(sender, "Sua solicitação foi enviada, aguarde um staff ir até você.");
    }
}
