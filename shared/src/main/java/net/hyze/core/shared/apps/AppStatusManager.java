package net.hyze.core.shared.apps;

import net.hyze.core.shared.CoreProvider;

public class AppStatusManager implements Runnable {

    private static final Object APP_STATUS_UPDATER_LOCKER = new Object();

    @Override
    public void run() {
        synchronized (APP_STATUS_UPDATER_LOCKER) {
            if (CoreProvider.getApp().getStatus() != null) {
                CoreProvider.Cache.Redis.APPS_STATUS.provide().update(CoreProvider.getApp());
            }
        }
    }
}
