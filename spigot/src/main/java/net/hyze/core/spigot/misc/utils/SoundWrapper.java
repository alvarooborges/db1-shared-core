package net.hyze.core.spigot.misc.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public class SoundWrapper {

    private final Sound sound;
    private final float volume, pitch;

    public void play(Player sender) {
        sender.playSound(sender.getLocation(), sound, volume, pitch);
    }
}
