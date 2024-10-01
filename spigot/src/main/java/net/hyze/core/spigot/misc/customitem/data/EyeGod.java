package net.hyze.core.spigot.misc.customitem.data;

import lombok.Getter;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.echo.packets.user.UserConnectPacket;
import net.hyze.core.shared.user.User;
import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.commands.events.ExecuteCustomCommandEvent;
import net.hyze.core.spigot.misc.combat.CombatManager;
import net.hyze.core.spigot.misc.customitem.CustomItem;
import net.hyze.core.spigot.misc.message.Message;
import net.hyze.core.spigot.misc.utils.InventoryUtils;
import net.hyze.core.spigot.misc.utils.ItemBuilder;
import net.hyze.core.spigot.misc.utils.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.greenrobot.eventbus.Subscribe;

public class EyeGod extends CustomItem implements Listener {

    public static String KEY = "custom_item_yey_good";
    private static boolean registeredEvents = false;

    @Getter
    private final ItemBuilder itemBuilder;

    private final SerializedLocation fallbackLocation;

    public EyeGod(SerializedLocation fallbackLocation) {
        super(KEY);

        this.fallbackLocation = fallbackLocation;

        this.itemBuilder = ItemBuilder.of(Material.EYE_OF_ENDER)
                .glowing(true)
                .name("&6" + this.getDisplayName());

        if (!registeredEvents) {
            Bukkit.getPluginManager().registerEvents(this, CoreSpigotPlugin.getInstance());
            registeredEvents = true;
        }
    }

    @Override
    public String getDisplayName() {
        return "Olho de Deus";
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

        if (CombatManager.isTagged(user)) {
            Message.ERROR.send(player, "Você não pode usar o Olho de Deus em combate.");
            return;
        }

        InventoryUtils.subtractOneOnHand(event);

        player.setGameMode(GameMode.SPECTATOR);
        player.setMetadata(KEY, new FixedMetadataValue(CoreSpigotPlugin.getInstance(), true));

        Message.SUCCESS.send(player, "Você usou o olho de Deus.");

        Location fallback = player.getLocation().clone();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.teleport(fallback);
                    player.removeMetadata(KEY, CoreSpigotPlugin.getInstance());
                    player.setGameMode(GameMode.SURVIVAL);

                    TeleportManager.teleport(
                            user,
                            fallbackLocation,
                            UserConnectPacket.Reason.PLUGIN,
                            "&eTeleportado ao spawn"
                    );
                }
            }
        }.runTaskLater(CoreSpigotPlugin.getInstance(), 20 * 15);
    }

    @EventHandler
    public void on0(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (player.hasMetadata(KEY) && player.getGameMode() == GameMode.SPECTATOR) {
            Message.ERROR.send(player, "Você não fazer isso enquanto usa o Olho de Deus.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on0(ExecuteCustomCommandEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getSender();

        if (player.hasMetadata(KEY) && player.getGameMode() == GameMode.SPECTATOR) {
            Message.ERROR.send(player, "Você não pode executar comandos enquanto usa o Olho de Deus.");
            event.setCancelled(true);
        }
    }
}
