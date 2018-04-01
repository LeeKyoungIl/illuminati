package com.leekyoungil.illuminati.ApiServerSample.container.handler;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public interface ContainerShutdownHandler {

    boolean isRunning();
    void stop();
}
