package net.hyze.core.shared.misc.timer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractTimer implements Runnable {

    @Getter
    protected final long tickDelay, initDelay;

    public abstract void start();

    public abstract void cancel();

}
