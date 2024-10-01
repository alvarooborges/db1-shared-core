package net.hyze.core.spigot.misc.stackmobs.listeners;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.spigot.CoreSpigotPlugin;
import net.hyze.core.spigot.misc.stackmobs.StackMobsAPI;
import net.hyze.core.spigot.misc.stackmobs.StackedEntity;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkLoadListener implements Listener {

    @EventHandler
    public void on(ChunkLoadEvent event) {
        if (!StackMobsAPI.WORLDS.containsEntry(CoreProvider.getApp().getId(), event.getWorld())) {
            return;
        }

        Set<Entity> entities = Sets.newHashSet(event.getChunk().getEntities());

        entities.stream()
                .filter(entity -> entity instanceof LivingEntity)
                .forEach(entity -> {
                    String name = entity.getCustomName();

                    if (name == null || name.isEmpty()) {
                        return;
                    }

                    name = MessageUtils.stripColor(name);

                    Pattern pattern = Pattern.compile("[0-9]+[x] ");
                    Matcher matcher = pattern.matcher(name);

                    if (!matcher.find()) {
                        return;
                    }

                    String rawSize = matcher.group(0).replaceAll("[^\\d]", "");

                    Integer size = Ints.tryParse(rawSize);

                    if (size == null) {
                        return;
                    }

                    EntityDeathListener.spawnNewEntity(size, 0, entity);
                    entity.remove();
                });
    }
}
