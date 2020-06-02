/*
 * Copyright 2017 Phoboslabs.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.phoboslabs.illuminati.backup.infra.backup.shutdown;

import me.phoboslabs.illuminati.processor.infra.backup.shutdown.handler.ContainerShutdownHandler;

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
        } catch (InterruptedException ignore) {}

        // broker connection close
        containerShutdownHandler.stop();
        // shutdown signal
        containerShutdownHandler.stopSignal();

        while (containerShutdownHandler.isRunning() && shutdownTimer.get() < threadLimitSleepTime) {
            try {
                Thread.sleep(threadSleepTime);
                shutdownTimer.addAndGet(threadSleepTime);
            } catch (InterruptedException ignore) {}
        }

        // set the 3 second term.
        try {
            Thread.sleep(endTermSleepTime);
        } catch (InterruptedException ignore) {}

        System.out.println("Illuminati BYE BYE...!");
    }
}
