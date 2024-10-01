package net.hyze.core.spigot.misc.frame;

import com.google.common.collect.Maps;
import net.hyze.core.spigot.CoreSpigotPlugin;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class FrameManager {

    public static HashMap<UUID, Frame> INTERACTABLE_FRAMES = Maps.newHashMap();
    public static HashMap<String, Frame> DATABASE_FRAMES = Maps.newHashMap();

    public static Frame getDatabaseFrame(String key) {
        return DATABASE_FRAMES.get(key);
    }

    public static Frame getInteractableFrame(ItemFrame entity) {
        for (Frame frame : INTERACTABLE_FRAMES.values()) {
            for (ItemFrame itemFrame : frame.getMapFrames()) {
                if (itemFrame.getEntityId() == entity.getEntityId()) {
                    return frame;
                }
            }
        }
        return null;
    }

    public static void reloadDatabaseFrames() {
        HashMap<String, Frame> currentDatabaseFrames = Maps.newHashMap(DATABASE_FRAMES);
        DATABASE_FRAMES.clear();
        Bukkit.getScheduler().runTaskAsynchronously(CoreSpigotPlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
//                FrameDatabase.loadFrames();
                Bukkit.getScheduler().runTask(CoreSpigotPlugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        currentDatabaseFrames.forEach((key, frame) -> {
                            if (frame != null && frame.isPlaced()) {
                                DATABASE_FRAMES.get(key).place(frame.getLocation(), frame.getBlockFace());
                            }
                        });
                    }
                });
            }
        });
    }

    public static void on(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof ItemFrame) {
            onInteract(event.getPlayer(), (ItemFrame) event.getRightClicked());
        }
    }

    public static void on(EntityDamageByEntityEvent event) {
        if ((event.getDamager() instanceof Player) && event.getEntity() instanceof ItemFrame) {
            onInteract((Player) event.getDamager(), (ItemFrame) event.getEntity());
        }
    }

    public static void onInteract(Player player, ItemFrame itemFrame) {
        Frame frame = getInteractableFrame(itemFrame);
        if (frame != null && frame.getInteractConsumer() != null) {
            frame.getInteractConsumer().accept(player);
        }
    }
}
