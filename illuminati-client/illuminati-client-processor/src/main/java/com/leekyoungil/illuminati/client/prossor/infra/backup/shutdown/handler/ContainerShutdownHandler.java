package com.leekyoungil.illuminati.client.prossor.infra.backup.shutdown.handler;

public interface ContainerShutdownHandler {

    boolean isRunning();
    void stop();
    void stopSignal();
}
