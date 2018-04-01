package com.leekyoungil.illuminati.ApiServerSample.container.listener.impl;

import com.leekyoungil.illuminati.ApiServerSample.container.SpringContainerGracefulShutdownChecker;
import com.leekyoungil.illuminati.ApiServerSample.container.listener.ContainerShutdownListener;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public class SpringContainerGracefulShutdownListener implements ContainerShutdownListener {

    private final SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker;

    public SpringContainerGracefulShutdownListener (SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker) {
        this.springContainerGracefulShutdownChecker = springContainerGracefulShutdownChecker;
    }

    @Override
    public void shutdown() {
        this.springContainerGracefulShutdownChecker.waitingForShutdown(1000l);
    }
}
