package net.hyze.core.spigot.misc.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class PlayerUtils {

    public static void clearInventory(Player player) {
        player.setItemOnCursor(null);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    /**
     * @param player Jogador a ser verificado.
     *
     * @return Verifica em todos os slots (Incluindo os de armadura, se ha um
     * item em algum deles). Retorna 'true' se nao encontrar nenhum item.
     */
    public static boolean isInventoryEmpty(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                return false;
            }
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }

    public static void clearEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static void healPlayer(Player player) {
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
    }

    public static void clear(Player player) {
        clearInventory(player);
        clearEffects(player);
        healPlayer(player);

        player.setExhaustion(0F);
        player.setFireTicks(0);

    }

    /*
     Retorna o player para qual o jogador está olhando
     */
    public static Player getLookingAt(Player player) {
        return getLookingAt(player, 5);
    }

    public static Player getLookingAt(Player player, int range) {
        return getLookingAt(player, 1, range);
    }

    public static Player getLookingAt(Player player, int minRange, int maxRange) {
        // NAO SEI O QUÃO ISSO PODE SER PESADO SE USADO NO PlayerMoveEvent
        // USAR COM CUIDADO

        minRange = Math.max(Math.min(minRange, maxRange), 1);
        maxRange = Math.max(minRange, maxRange);

        Vector dir = player.getLocation().getDirection();
        Location loc = player.getEyeLocation().clone();

        for (int i = 0; i < minRange; i++) {
            loc.add(dir.getX(), dir.getY(), dir.getZ());
        }

        try {
            for (int i = minRange; i <= maxRange; i++) {
                loc.add(dir.getX(), dir.getY(), dir.getZ());

                Collection<Entity> nearby = loc.getWorld().getNearbyEntities(loc, 1, 1, 1);

                if (nearby != null && !nearby.isEmpty()) {

                    Optional<Entity> playerFound = nearby.stream()
                            .filter(Objects::nonNull)
                            .filter(target -> target instanceof Player && !target.getName().equals(player.getName()))
                            .findFirst();

                    if (playerFound.isPresent()) {
                        return (Player) playerFound.get();
                    }
                }
            }
        } catch (NullPointerException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Player[] getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        list.addAll(Bukkit.getOnlinePlayers());
        return list.toArray(new Player[list.size()]);
    }

    public static Player[] getOnlinePlayers(World world) {
        List<Player> list = new ArrayList<>();
        list.addAll(world.getPlayers());
        return list.toArray(new Player[list.size()]);
    }

}
