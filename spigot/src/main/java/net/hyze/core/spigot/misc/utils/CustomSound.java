package net.hyze.core.spigot.misc.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public enum CustomSound {

    BAD(new SoundWrapper(Sound.VILLAGER_NO, 1.0f, 1.0f)),
    GOOD(new SoundWrapper(Sound.NOTE_PIANO, 1.0f, 1.0f)),
    ROAAAR(new SoundWrapper(Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f));

    private final SoundWrapper sound;

    public void play(Player player) {
        sound.play(player);
    }

}
