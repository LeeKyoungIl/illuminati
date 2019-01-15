package me.phoboslabs.illuminati.client.prossor.infra.backup.shutdown.handler;

/**
 *  - @marcus.moon provided me with an Graceful idea.
 *
 * Created by leekyoungil (leekyoungil@gmail.com) on 04/05/2018.
 */
public interface ContainerShutdownHandler {

    boolean isRunning();
    void stop();
    void stopSignal();
}
