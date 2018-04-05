package com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * - @marcus.moon provided me with an Graceful idea.
 * 
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class IlluminatiGracefulShutdownChecker {

    private final static AtomicBoolean ILLUMINATI_READY_TO_SHUTDOWN = new AtomicBoolean(false);

    public IlluminatiGracefulShutdownChecker() {}

    public static boolean getIlluminatiReadyToShutdown() {
        return ILLUMINATI_READY_TO_SHUTDOWN.get();
    }

    public static void setIlluminatiReadyToShutdown (boolean readyToShutdown) {
        ILLUMINATI_READY_TO_SHUTDOWN.lazySet(readyToShutdown);
    }
}
