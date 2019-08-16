package me.phoboslabs.illuminati.prossor.infra.backup.shutdown;

import me.phoboslabs.illuminati.prossor.infra.backup.shutdown.handler.ContainerShutdownHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * - @marcus.moon provided me with an Graceful idea.
 *
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public class ContainerSignalHandler extends Thread {

    private final ContainerShutdownHandler containerShutdownHandler;

    private final long threadSleepTime = 1000l;
    private final long threadLimitSleepTime = 60000l;
    private final long endTermSleepTime = 2000l;
    private final AtomicLong shutdownTimer = new AtomicLong(0l);

    public ContainerSignalHandler(ContainerShutdownHandler containerShutdownHandler) {
        this.containerShutdownHandler = containerShutdownHandler;
    }

    public void run() {
        System.out.println("Illuminati is preparing to close...");
        // off illuminati
        IlluminatiGracefulShutdownChecker.setIlluminatiReadyToShutdown(true);
        // set the 3 second term.
        try {
            Thread.sleep(endTermSleepTime);
        } catch (InterruptedException ignored) {
            // ignored...
        }

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
