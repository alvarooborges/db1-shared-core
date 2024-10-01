package net.hyze.core.spigot.misc.customitem.data;

import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

public class PropellantItem extends CustomItem {

    @Getter
    private final ItemBuilder itemBuilder;

    public static String KEY = "custom_item_propellant";

    public PropellantItem() {
        super(KEY);

        this.itemBuilder = ItemBuilder.of(Material.FIREWORK)
                .glowing(true)
                .name("&aPropulsor")
                .lore("Impulsiona você e todos", "os jogadores próximos", "em 20 blocos para onde", "você estiver mirando.")
                .lore("")
                .lore("&fRaio da Propulsão: &65 blocos");
    }

    @Override
    public String getDisplayName() {
        return "&aPropulsor";
    }

    @Subscribe
    public void on(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        if (!UserCooldowns.hasEnded(user, "user-propellant-item")) {
            Message.ERROR.send(player, String.format("Aguarde %s para usar o %s novamente.",
                    UserCooldowns.getFormattedTimeLeft(user, "user-propellant-item"),
                    getDisplayName()
            ));
            return;
        }

        Vector vector = player.getLocation().getDirection().clone().multiply(2.8);
        vector.setY(0.5);
        player.setVelocity(vector);

        player.getNearbyEntities(2, 2, 2).stream()
                .filter(entity -> entity instanceof Player)
                .forEach(target -> {
                    target.setVelocity(vector);
                });

        InventoryUtils.subtractOneOnHand(event);

        player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 5, 2);

        Bukkit.getScheduler().runTaskAsynchronously(CoreSpigotPlugin.getInstance(), () -> {
            for (int i = 0; i < 200; i++) {
                player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 1, 5);
            }
        });

        UserCooldowns.start(user, "user-propellant-item", 15, TimeUnit.SECONDS);
    }
}
