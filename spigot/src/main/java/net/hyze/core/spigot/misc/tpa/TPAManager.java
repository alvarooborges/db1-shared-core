package net.hyze.core.spigot.misc.tpa;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.shared.echo.packets.tpa.TPAPacket;
import net.hyze.core.shared.echo.packets.tpa.TPAcceptPacket;
import net.hyze.core.shared.echo.packets.tpa.TPCancelPacket;
import net.hyze.core.shared.echo.packets.tpa.TPDenyPacket;
import net.hyze.core.shared.echo.packets.user.UserConnectPacket;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import net.hyze.core.spigot.world.location.unserializer.BukkitLocationParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

public class TPAManager implements EchoListener {

    private static final Multimap<String, String> REQUESTS = ArrayListMultimap.create();

    @Subscribe
    public void on(TPAPacket packet) {
        REQUESTS.put(packet.getTarget().getNick(), packet.getRequester().getNick());

        Bukkit.getScheduler().runTaskLater(CoreSpigotPlugin.getInstance(), () -> {
            REQUESTS.remove(packet.getTarget().getNick(), packet.getRequester().getNick());
        }, 60 * 20);

        Player targetPlayer = Bukkit.getPlayerExact(packet.getTarget().getNick());

        if (targetPlayer != null && targetPlayer.isOnline()) {
            ComponentBuilder builder = new ComponentBuilder("\n")
                    .append(packet.getRequester().getNick()).color(ChatColor.GRAY)
                    .append(" lhe enviou um pedido de teletransporte!").color(ChatColor.YELLOW)
                    .append("\n")
                    .append("Clique").color(ChatColor.YELLOW)
                    .append(" AQUI").color(ChatColor.GREEN).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + packet.getRequester().getNick()))
                    .event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new BaseComponent[]{
                                new TextComponent(MessageUtils.translateColorCodes(String.format(
                                        "&aClique e aceite o pedido \n&ade teletransporte de %s.", packet.getRequester().getNick()
                                )))
                            }
                    ))
                    .append(" para aceitar ou").color(ChatColor.YELLOW)
                    .append(" AQUI").color(ChatColor.RED).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + packet.getRequester().getNick()))
                    .append(" para negar.").color(ChatColor.YELLOW)
                    .append("\n");

            targetPlayer.spigot().sendMessage(builder.create());
        }
    }

    @Subscribe
    public void on(TPDenyPacket packet) {
        REQUESTS.remove(packet.getTarget().getNick(), packet.getRequester().getNick());

        Player requesterPlayer = Bukkit.getPlayerExact(packet.getRequester().getNick());

        if (requesterPlayer != null && requesterPlayer.isOnline()) {
            Message.INFO.send(requesterPlayer, "Pedido de teleporte para " + packet.getTarget().getNick() + " negado.");
        }
    }

    @Subscribe
    public void on(TPCancelPacket packet) {
        REQUESTS.remove(packet.getTarget().getNick(), packet.getRequester().getNick());

        Player targetPlayer = Bukkit.getPlayerExact(packet.getTarget().getNick());

        if (targetPlayer != null && targetPlayer.isOnline()) {
            Message.INFO.send(targetPlayer, "Pedido de teleporte cancelado por " + packet.getRequester().getNick() + ".");
        }
    }

    @Subscribe
    public void on(TPAcceptPacket packet) {
        REQUESTS.remove(packet.getTarget().getNick(), packet.getRequester().getNick());

        Player target = Bukkit.getPlayerExact(packet.getTarget().getNick());

        if (target != null && target.isOnline()) {

            TeleportManager.teleport(
                    packet.getRequester(),
                    BukkitLocationParser.serialize(target.getLocation()),
                    UserConnectPacket.Reason.TPA,
                    String.format("&aPedido de teleporte aceito por %s.", packet.getTarget().getNick())
            );
        }
    }

    public static boolean hasRequest(User requester, User target) {
        return hasRequest(requester.getNick(), target.getNick());
    }

    public static boolean hasRequest(String requester, String target) {
        return REQUESTS.containsEntry(target, requester);
    }
}
