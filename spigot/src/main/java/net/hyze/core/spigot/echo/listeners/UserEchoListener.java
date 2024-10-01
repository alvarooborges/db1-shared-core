package net.hyze.core.spigot.echo.listeners;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.api.EchoListener;
import net.hyze.core.shared.echo.packets.user.UserLoggedPacket;
import net.hyze.core.shared.echo.packets.user.UserTellPacket;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.echo.packets.UserLocationRequest;
import net.hyze.core.spigot.echo.packets.UserPreferenceUpdatePacket;
import net.hyze.core.spigot.misc.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.greenrobot.eventbus.Subscribe;

public class UserEchoListener implements EchoListener {

    @Subscribe
    public void on(UserTellPacket packet) {
        Player player = Bukkit.getPlayerExact(packet.getSender().getNick());

        String message = MessageUtils.stripColor(MessageUtils.translateColorCodes(packet.getMessage()));

        if (player != null) {
            Message.EMPTY.send(player, "&8Mensagem para " + packet.getTarget().getHighestGroup().getColor() + packet.getTarget().getNick() + "&7: " + message);
        }

        Player target = Bukkit.getPlayerExact(packet.getTarget().getNick());

        if (target != null) {
            Message.EMPTY.send(target, "&8Mensagem de " + packet.getSender().getHighestGroup().getColor() + packet.getSender().getNick() + "&7: " + message);
        }
    }

    @Subscribe
    public void on(UserPreferenceUpdatePacket packet) {
        CoreProvider.Cache.Local.USERS_PREFERENCES.provide().refresh(packet.getUser());
    }

    @Subscribe
    public void on(UserLocationRequest packet) {
        Player player = Bukkit.getPlayerExact(packet.getUser().getNick());

        if (player != null && player.isOnline()) {
            Location location = player.getLocation();

            packet.setResponse(new UserLocationRequest.UserLocationResponse(new SerializedLocation(
                    CoreProvider.getApp().getId(),
                    location.getWorld().getName(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    location.getYaw(),
                    location.getPitch()
            )));
        }
    }

//    @Subscribe
//    public void on(UserConnectPacket packet) {
//	if (!CoreProvider.getApp().equals(packet.getTargetApp())) {
//	    return;
//	}
//
//	Player player = Bukkit.getPlayerExact(packet.getLeader().getNick());
//
//	if (player == null || !player.isOnline()) {
//	    return;
//	}
//	player.teleport(packet.getSerializedLocation().parser(CoreSpigotConstants.LOCATION_PARSER));
//    }
    @Subscribe
    public void on(UserLoggedPacket packet) {
        packet.getUser().setLogged(true);
    }
}
