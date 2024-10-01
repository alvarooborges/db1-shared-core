package net.hyze.core.spigot.misc.mining;

import net.hyze.core.shared.CoreConstants;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MiningXP {

    private final int base;
    private final int random;

    public MiningXP(int base) {
        this.base = base;
        this.random = 0;
    }

    public int getXP() {
        return base + (random > 0 ? CoreConstants.RANDOM.nextInt(random) : 0);
    }

}
