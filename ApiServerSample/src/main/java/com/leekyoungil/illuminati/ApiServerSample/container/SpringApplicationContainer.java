package com.leekyoungil.illuminati.ApiServerSample.container;

import com.leekyoungil.illuminati.ApiServerSample.container.config.SpringContainerGracefulShutdownConfig;
import com.leekyoungil.illuminati.ApiServerSample.container.handler.impl.SpringContainerShutdownHandler;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;

/**
 *  - i refer to the source that @marcus.moon created.
 */
@Import({SpringContainerGracefulShutdownConfig.class})
public class SpringApplicationContainer {

    public static void run(Class<?> mainClass, String[] args) {
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder()
                .main(mainClass)
                .sources(mainClass, SpringContainerGracefulShutdownConfig.class)
                .registerShutdownHook(true)
                .run(args);

//        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//            public void run() {
//                try {
//                    while (true) {
//                        System.out.println("test1");
//                        Thread.sleep(1000);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }));
        if(applicationContext.isActive()) {
            if (applicationContext instanceof EmbeddedWebApplicationContext) {
                EmbeddedWebApplicationContext embeddedWebApplicationContext = (EmbeddedWebApplicationContext)applicationContext;
                ContainerSignalHandler.install("USR2", new SpringContainerShutdownHandler(embeddedWebApplicationContext));
            }
        }
    }
}
