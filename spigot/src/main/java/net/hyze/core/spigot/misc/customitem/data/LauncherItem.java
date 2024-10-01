package net.hyze.core.spigot.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.CoreSpigotConstants;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

public class LauncherItem extends CustomItem {

    @Getter
    private final ItemBuilder itemBuilder;

    public static String KEY = "custom_item_laucher";

    public LauncherItem() {
        super(KEY);

        this.itemBuilder = ItemBuilder.of(Material.FIREWORK)
                .glowing(true)
                .name("&aLançador")
                .lore("Ao utilizar este item você", "será arremessado em 60", "blocos para cima!");
    }

    @Override
    public String getDisplayName() {
        return "&aLançador";
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

        if (!UserCooldowns.hasEnded(user, "user-launcher-item")) {
            return;
        }

        double up = 4;
        player.setVelocity(new Vector(0, up, 0));

        InventoryUtils.subtractOneOnHand(event);

        player.getWorld().playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 5, 0);

        Bukkit.getScheduler().runTaskAsynchronously(CoreSpigotPlugin.getInstance(), () -> {
            for (int i = 0; i < 200; i++) {
                player.getWorld().playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 1, 5);
            }
        });

        player.setMetadata(CoreSpigotConstants.NBTKeys.PLAYER_FALL_DAMAGE_BYPASS, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));

        UserCooldowns.start(user, "user-launcher-item", 1, TimeUnit.SECONDS);
    }
}
