package com.leekyoungil.illuminati.ApiServerSample.container;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public class SpringContainerGracefulShutdownChecker {

    private final AtomicBoolean shutdownStarted = new AtomicBoolean(false);
    private final AtomicInteger requestedCount = new AtomicInteger(0);

    public SpringContainerGracefulShutdownChecker () {}

    public boolean shutdownStarted() {
        return shutdownStarted.get();
    }

    public void increaseRequestCount () {
        this.requestedCount.incrementAndGet();
    }

    public void decreaseRequestCount () {
        this.requestedCount.decrementAndGet();
    }

    public void waitingForShutdown (long waitSec) {
        System.out.println("test");
    }
}
