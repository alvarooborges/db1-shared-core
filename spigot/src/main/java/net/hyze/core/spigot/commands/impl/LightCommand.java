package net.hyze.core.spigot.commands.impl;

import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.commands.Argument;
import net.hyze.core.shared.commands.CommandRestriction;
import net.hyze.core.shared.commands.GroupCommandRestrictable;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.commands.CustomCommand;
import net.hyze.core.spigot.misc.message.Message;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LightCommand extends CustomCommand implements GroupCommandRestrictable, Listener {

    public final Group group;

    private final BiConsumer<User, Boolean> statusDefiner;
    private final Predicate<User> statusTester;

    public static final Set<String> ACTIVATED_USERS_PERMANENT = Sets.newHashSet();

    public LightCommand(Group group, BiConsumer<User, Boolean> setStatus, Predicate<User> getStatus) {
        super("luz", CommandRestriction.IN_GAME);

        registerArgument(new Argument("on|off", "Estado da luz", true));

        this.group = group;
        this.statusDefiner = setStatus;
        this.statusTester = getStatus;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (this.statusTester != null) {
            User user = CoreProvider.Cache.Local.USERS.provide().get(event.getPlayer().getName());
            turn(event.getPlayer(), this.statusTester.test(user), false);
        }
    }

    @Override
    public void onCommand(CommandSender sender, User user, String[] args) {

        if (CoreSpigotConstants.STOPPING) {
            return;
        }

        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "on":
                if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION) && this.statusTester.test(user)) {
                    Message.ERROR.send(sender, "Sua luz já está ativa.");
                    return;
                }

                this.statusDefiner.accept(user, true);

                turn(player, true, true);
                Message.INFO.send(sender, "Você ativou a luz.");
                break;

            case "off":
                if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION) && !this.statusTester.test(user)) {
                    Message.ERROR.send(sender, "Sua luz já está desativada.");
                    return;
                }

                this.statusDefiner.accept(user, false);

                turn(player, false, true);
                Message.ERROR.send(sender, "Luz desativada.");
                break;

            default:
                Message.ERROR.send(sender, "Utilize: /luz [on/off]");
                return;
        }
    }

    public static void turn(Player player, boolean value, boolean sound) {
        if (value) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1));

            if (sound) {
                player.playSound(player.getLocation(), Sound.CLICK, 1, 2);
            }
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);

            if (sound) {
                player.playSound(player.getLocation(), Sound.CLICK, 1, 0);
            }
        }
    }

    @Override
    public Group getGroup() {
        return this.group;
    }
}
