package net.hyze.core.spigot.misc.hologram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HologramPosition {

    UP(0.23D), DOWN(-0.29D);

    private final double value;

}
