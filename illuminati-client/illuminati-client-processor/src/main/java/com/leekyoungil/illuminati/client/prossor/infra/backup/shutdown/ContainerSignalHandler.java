package com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown;

import com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown.handler.ContainerShutdownHandler;

import java.util.concurrent.atomic.AtomicLong;

public class ContainerSignalHandler extends Thread {

    private final ContainerShutdownHandler containerShutdownHandler;

    private final long threadSleepTime = 1000l;
    private final long threadLimitSleepTime = 60000l;
    private final long endTermSleepTime = 3000l;
    private final AtomicLong shutdownTimer = new AtomicLong(0l);

    public ContainerSignalHandler(ContainerShutdownHandler containerShutdownHandler) {
        this.containerShutdownHandler = containerShutdownHandler;
    }

    public void run() {
        System.out.println("Illuminati is preparing to close...");
        // off illuminati
        IlluminatiGracefulShutdownChecker.setIlluminatiReadyToShutdown(true);
        // broker connection close
        containerShutdownHandler.stop();
        // shutdown signal
        containerShutdownHandler.stopSignal();

        while (containerShutdownHandler.isRunning() && shutdownTimer.get() < threadLimitSleepTime) {
            try {
                Thread.sleep(threadSleepTime);
                shutdownTimer.addAndGet(threadSleepTime);
            } catch (InterruptedException ignored) {
                // ignored...
            }
        }

        // set the 3 second term.
        try {
            Thread.sleep(endTermSleepTime);
        } catch (InterruptedException ignored) {
            // ignored...
        }

        System.out.println("Illuminati BYE BYE...!");
    }
}
