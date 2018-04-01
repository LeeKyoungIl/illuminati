package com.leekyoungil.illuminati.ApiServerSample.container;

import com.leekyoungil.illuminati.ApiServerSample.container.handler.ContainerShutdownHandler;
import com.leekyoungil.illuminati.ApiServerSample.container.listener.ContainerShutdownListener;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public class ContainerSignalHandler implements SignalHandler {

    private final SignalHandler oldSignalHandler;
    private final ContainerShutdownHandler containerShutdownHandler;

    private final static List<ContainerShutdownListener> SIGNAL_LISTENERS = new ArrayList<ContainerShutdownListener>();

    private ContainerSignalHandler (String signalName, ContainerShutdownHandler containerShutdownHandler) {
        Signal signal = new Signal(signalName);
        this.containerShutdownHandler = containerShutdownHandler;

        this.oldSignalHandler = Signal.handle(signal, this);
    }

    public static ContainerSignalHandler install (String signalName, ContainerShutdownHandler containerShutdownHandler) {
        if (StringUtils.isEmpty(signalName)) {
            throw new IllegalArgumentException("signalName can not be null.");
        }
        return new ContainerSignalHandler(signalName, containerShutdownHandler);
    }

    public static void addListener (ContainerShutdownListener containerShutdownListener) {
        SIGNAL_LISTENERS.add(containerShutdownListener);
    }

    @Override
    public void handle(Signal signal) {
        final String containerName = this.containerShutdownHandler.getClass().getSimpleName();

        if (CollectionUtils.isNotEmpty(SIGNAL_LISTENERS)) {
            for (ContainerShutdownListener containerShutdownListener : SIGNAL_LISTENERS) {
                containerShutdownListener.shutdown();
            }
        }

        while (this.containerShutdownHandler.isRunning()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) {
                // ignored...
            }
        }

        if (oldSignalHandler != SIG_DFL && oldSignalHandler != SIG_IGN) {
            oldSignalHandler.handle(signal);
        }
    }
}
