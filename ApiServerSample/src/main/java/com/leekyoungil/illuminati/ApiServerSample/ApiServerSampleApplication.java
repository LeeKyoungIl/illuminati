package com.leekyoungil.illuminati.ApiServerSample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAutoConfiguration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.leekyoungil"})
public class ApiServerSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiServerSampleApplication.class, args);
    }

}
