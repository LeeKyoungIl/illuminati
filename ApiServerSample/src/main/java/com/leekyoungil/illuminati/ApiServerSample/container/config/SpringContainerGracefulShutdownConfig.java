package com.leekyoungil.illuminati.ApiServerSample.container.config;

import com.google.common.collect.Sets;
import com.leekyoungil.illuminati.ApiServerSample.container.ContainerSignalHandler;
import com.leekyoungil.illuminati.ApiServerSample.container.SpringContainerGracefulShutdownChecker;
import com.leekyoungil.illuminati.ApiServerSample.container.filter.SpringContainerGracefulShutdownFilter;
import com.leekyoungil.illuminati.ApiServerSample.container.listener.impl.SpringContainerGracefulShutdownListener;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

import javax.servlet.DispatcherType;

/**
 *  - i refer to the source that @marcus.moon created.
 */
@Configuration
public class SpringContainerGracefulShutdownConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Bean
    public SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker() {
        return new SpringContainerGracefulShutdownChecker();
    }

    @Bean
    public FilterRegistrationBean gracefulShutdownFilter(SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker) {
        SpringContainerGracefulShutdownFilter springContainerGracefulShutdownFilter =
                new SpringContainerGracefulShutdownFilter(springContainerGracefulShutdownChecker);

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setName("springContainerGracefulShutdownFilter");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterRegistrationBean.setFilter(springContainerGracefulShutdownFilter);
        filterRegistrationBean.setUrlPatterns(Sets.newHashSet("/*"));
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);

        return filterRegistrationBean;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker = contextRefreshedEvent.getApplicationContext().getBean(SpringContainerGracefulShutdownChecker.class);
        ContainerSignalHandler.addListener(new SpringContainerGracefulShutdownListener(springContainerGracefulShutdownChecker));
    }
}
