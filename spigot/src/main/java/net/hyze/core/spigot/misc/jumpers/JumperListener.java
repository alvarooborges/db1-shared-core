package net.hyze.core.spigot.misc.jumpers;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import java.util.concurrent.TimeUnit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class JumperListener implements Listener {

    private final String COOLDOWN_KEY = "JUMPERS";

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if (LocationUtils.compareLocation(event.getFrom(), event.getTo())) {
            return;
        }

        Location floor = event.getTo().clone();
        floor.subtract(0, 1, 0);

        if (!Material.SLIME_BLOCK.equals(floor.getBlock().getType())) {
            return;
        }

        Block block = floor.getBlock();

        if (!block.hasMetadata(Jumper.IDENTIFIER)) {
            return;
        }

        Player player = event.getPlayer();
        User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());

        if (!UserCooldowns.hasEnded(user, COOLDOWN_KEY)) {
            return;
        }

        UserCooldowns.start(user, COOLDOWN_KEY, 2, TimeUnit.SECONDS);

        Location location = (Location) block.getMetadata(Jumper.IDENTIFIER).get(0).value();
        int multiply = (int) block.getMetadata(Jumper.IDENTIFIER_MULTIPLY).get(0).value();
        int y = (int) block.getMetadata(Jumper.IDENTIFIER_Y).get(0).value();

        Vector target = location.toVector().subtract(player.getLocation().toVector()).normalize();
        target.multiply(multiply);
        target.setY(y);

        player.setVelocity(target);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1L, 2L);

    }

}
