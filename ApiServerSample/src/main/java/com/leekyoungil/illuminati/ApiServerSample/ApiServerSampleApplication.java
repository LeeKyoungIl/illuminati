package com.leekyoungil.illuminati.ApiServerSample;

import com.leekyoungil.illuminati.ApiServerSample.container.SpringApplicationContainer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@ComponentScan(basePackages = {"com.leekyoungil"})
public class ApiServerSampleApplication {

    public static void main(String[] args) {
        SpringApplicationContainer.run(ApiServerSampleApplication.class, args);
        //SpringApplication.run(ApiServerSampleApplication.class, args);
    }

}
