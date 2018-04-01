package com.leekyoungil.illuminati.ApiServerSample.container.handler.impl;

import com.leekyoungil.illuminati.ApiServerSample.container.handler.ContainerShutdownHandler;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public class SpringContainerShutdownHandler implements ContainerShutdownHandler {

    private final EmbeddedWebApplicationContext applicationContext;

    public SpringContainerShutdownHandler (EmbeddedWebApplicationContext embeddedWebApplicationContext) {
        this.applicationContext = embeddedWebApplicationContext;
    }

    @Override public boolean isRunning() {
        return this.applicationContext.isRunning();
    }

    @Override public void stop() {
        this.applicationContext.close();
    }
}
