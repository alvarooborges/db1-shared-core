package net.hyze.core.spigot.misc.hiddencuboid;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.utils.LocationUtils;
import net.hyze.core.spigot.misc.utils.WorldCuboid;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HiddenCuboidManager implements Listener {

    private static final Multimap<String, WorldCuboid> HIDDEN_CUBOIDS = HashMultimap.create();
    private static final List<Function<Player, HiddenResult>> HIDDEN_FUNCTIONS = Lists.newArrayList();
    private static final List<BiFunction<Player, Player, HiddenResult>> HIDDEN_BI_FUNCTIONS = Lists.newArrayList();

    public static void registerHiddenFunction(Function<Player, HiddenResult> function) {
        HIDDEN_FUNCTIONS.add(function);
    }

    public static void registerHiddenBiFunction(BiFunction<Player, Player, HiddenResult> biFunction) {
        HIDDEN_BI_FUNCTIONS.add(biFunction);
    }

    public static void registerHiddenCuboid(String appId, WorldCuboid cuboid) {
        HIDDEN_CUBOIDS.put(appId, cuboid);
    }

    public HiddenCuboidManager() {
        Bukkit.getScheduler().runTaskTimer(CoreSpigotPlugin.getInstance(), () -> {
            updatePlayers(Bukkit.getOnlinePlayers());
        }, 20L, 20L);
    }

    private void updatePlayers(Collection<? extends Player> players) {
        if (players.isEmpty()) {
            return;
        }
        
        Set<Player> toHide = Sets.newHashSet();

        for (Player player : players) {
            World world = player.getWorld();

            boolean hidePlayer = HIDDEN_CUBOIDS.get(CoreProvider.getApp().getId()).stream()
                    .filter(cuboid -> cuboid.getWorldName().equalsIgnoreCase(world.getName()))
                    .anyMatch(cuboid -> cuboid.contains(player.getLocation(), true));

            HiddenResult result = HIDDEN_FUNCTIONS.stream()
                    .map(function -> function.apply(player))
                    .reduce(HiddenResult.NONE, (current, toReduce) -> {
                        if (current != HiddenResult.SHOW) {
                            return toReduce;
                        }

                        return current;
                    });

            hidePlayer = hidePlayer && result != HiddenResult.SHOW;

            if (hidePlayer) {
                toHide.add(player);
            }
        }

        for (Player player : players) {
            Set<Player> toHideStrict = Sets.newHashSet(toHide);
            Set<Player> toShowStrict = Sets.newHashSet();

            for (Player target : Bukkit.getOnlinePlayers()) {
                HiddenResult result = HIDDEN_BI_FUNCTIONS.stream()
                        .map(biFunction -> biFunction.apply(player, target))
                        .reduce(HiddenResult.NONE, (current, toReduce) -> {
                            if (current != HiddenResult.SHOW) {
                                return toReduce;
                            }

                            return current;
                        });

                if (result == HiddenResult.HIDE) {
                    toHideStrict.add(target);
                }

                if (result == HiddenResult.SHOW || !toHideStrict.contains(target)) {
                    toHideStrict.remove(target);
                    toShowStrict.add(target);
                }
            }

            toHideStrict.forEach(player::hidePlayer);
            toShowStrict.forEach(player::showPlayer);
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if (LocationUtils.compareLocation(event.getTo(), event.getFrom())) {
            return;
        }

        updatePlayers(Collections.singleton(event.getPlayer()));
    }


//    @EventHandler
//    public void on(PlayerJoinEvent event) {
//        World world = event.getPlayer().getWorld();
//
//        Bukkit.getScheduler().runTask(CoreSpigotPlugin.getInstance(), () -> {
//            HIDDEN_CUBOIDS.get(CoreProvider.getApp().getId()).stream()
//                    .filter(cuboid -> cuboid.getWorldName().equalsIgnoreCase(world.getName()))
//                    .forEach(cuboid -> hide(event.getPlayer(), cuboid));
//        });
//    }

//    private void hide(Player player, WorldCuboid cuboid) {
//        cuboid.getEntities(entity -> entity instanceof Player)
//                .stream()
//                .map(entity -> (Player) entity)
//                .filter(target -> !target.equals(player))
//                .forEach(player::hidePlayer);
//    }
}
