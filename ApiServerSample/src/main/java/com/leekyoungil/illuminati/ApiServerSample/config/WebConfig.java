package com.leekyoungil.illuminati.ApiServerSample.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 23/06/2017.
 *  - github : https://github.com/LeeKyoungIl
 */
@EnableWebMvc
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WebConfig extends WebMvcConfigurerAdapter {
//
//    @Override
//    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
//        interceptorRegistry.addInterceptor(getIlluminatiManager())
//                .addPathPatterns("/**");
////                .excludePathPatterns("")
//    }
}
