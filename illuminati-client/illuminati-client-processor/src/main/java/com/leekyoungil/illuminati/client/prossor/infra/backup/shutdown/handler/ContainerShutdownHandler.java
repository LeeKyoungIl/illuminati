package com.leekyoungil.illuminati.common.shutdown.handler;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public interface ContainerShutdownHandler {

    boolean isRunning();
    void stop();
}
