package net.hyze.core.spigot.misc.party;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.shared.echo.packets.party.answer.RPartyAnswerPacket;
import net.hyze.core.shared.echo.packets.party.disband.RPartyDisbandPacket;
import net.hyze.core.shared.echo.packets.party.invite.RPartyInvitePacket;
import net.hyze.core.shared.echo.packets.party.answer.AbstractPartyAnswerPacket;
import net.hyze.core.shared.echo.packets.party.leave.RPartyLeavePacket;
import net.hyze.core.shared.echo.packets.party.promote.RPartyPromotePacket;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.party.Party;
import net.hyze.core.shared.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PartyManager implements EchoListener {

    @Subscribe
    public void on(RPartyInvitePacket packet) {
        User requester = packet.getRequester();
        User target = packet.getTarget();

        Player targetPlayer = Bukkit.getPlayerExact(target.getNick());
        if(targetPlayer != null && targetPlayer.isOnline()) {
            ComponentBuilder builder = new ComponentBuilder("\n")
                    .append(packet.getRequester().getNick()).color(ChatColor.GOLD)
                    .append(" lhe convidou para uma Party!").color(ChatColor.YELLOW)
                    .append("\n")
                    .append("Clique").color(ChatColor.YELLOW)
                    .append(" AQUI").color(ChatColor.GREEN).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party aceitar " + packet.getRequester().getNick()))
                    .event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new BaseComponent[]{
                                    new TextComponent(MessageUtils.translateColorCodes(String.format(
                                            "&aClique e aceite o pedido \n&ade party de %s.", packet.getRequester().getNick()
                                    )))
                            }
                    ))
                    .append(" para aceitar ou").color(ChatColor.YELLOW)
                    .append(" AQUI").color(ChatColor.RED).bold(true)
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party rejeitar " + packet.getRequester().getNick()))
                    .append(" para negar.").color(ChatColor.YELLOW)
                    .append("\n");

            targetPlayer.spigot().sendMessage(builder.create());
        }
    }

    @Subscribe
    public void on(RPartyAnswerPacket packet) {
        User requester = packet.getRequester();
        User target = packet.getTarget();

        Player requesterPlayer = Bukkit.getPlayerExact(requester.getNick());
        if(requesterPlayer != null && requesterPlayer.isOnline()) {
            requesterPlayer.sendMessage(MessageUtils.translateColorCodes(String.format(packet.getAnswer().getReceiverMessage(), target.getNick())));
        }

        if(packet.getAnswer() == AbstractPartyAnswerPacket.EnumAnswer.IGNORED) {
            Player targetPlayer = Bukkit.getPlayerExact(target.getNick());
            if(targetPlayer != null) {
                targetPlayer.sendMessage(MessageUtils.translateColorCodes(String.format(packet.getAnswer().getTransmitterMessage(), requester.getNick())));
            }
        }
    }

    @Subscribe
    public void on(RPartyDisbandPacket packet) {
        User leader = packet.getLeader();

        Set<Integer> members = packet.getMembers();
        Map<Integer, User> users = CoreProvider.Cache.Local.USERS.provide().getAllPresentByIds(members);

        String message = MessageUtils.translateColorCodes(String.format("&c%s eliminou a party.", leader.getNick()));

        users.values().stream().map(User::getNick).map(Bukkit::getPlayerExact).filter(Objects::nonNull).forEach((responsePlayer) -> {
            responsePlayer.sendMessage(message);
        });
    }

    @Subscribe
    public void on(RPartyPromotePacket packet) {
        User leader = packet.getLeader();

        Set<Integer> members = packet.getMembers();
        Map<Integer, User> users = CoreProvider.Cache.Local.USERS.provide().getAllPresentByIds(members);

        String message = MessageUtils.translateColorCodes(String.format("&6%s &aé o novo líder da party.", leader.getNick()));

        users.values().stream().map(User::getNick).map(Bukkit::getPlayerExact).filter(Objects::nonNull).forEach((responsePlayer) -> {
            responsePlayer.sendMessage(message);
        });
    }

    @Subscribe
    public void on(RPartyLeavePacket packet) {
        User target = packet.getTarget();

        Set<Integer> members = packet.getMembers();
        Map<Integer, User> users = CoreProvider.Cache.Local.USERS.provide().getAllPresentByIds(members);

        users.values().stream().map(User::getNick).map(Bukkit::getPlayerExact).filter(Objects::nonNull).forEach((responsePlayer) -> {
            responsePlayer.sendMessage(MessageUtils.translateColorCodes(String.format(packet.getReason().getMessage(), target.getNick())));
        });
    }
}
