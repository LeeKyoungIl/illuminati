package com.leekyoungil.illuminati.ApiServerSample.container.filter;

import com.leekyoungil.illuminati.ApiServerSample.container.SpringContainerGracefulShutdownChecker;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  - i refer to the source that @marcus.moon created.
 */
public class SpringContainerGracefulShutdownFilter extends OncePerRequestFilter {

    private final SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker;

    public SpringContainerGracefulShutdownFilter (SpringContainerGracefulShutdownChecker springContainerGracefulShutdownChecker) {
        this.springContainerGracefulShutdownChecker = springContainerGracefulShutdownChecker;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (springContainerGracefulShutdownChecker.shutdownStarted()) {
            httpServletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return;
        }

        try {
            springContainerGracefulShutdownChecker.increaseRequestCount();
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            springContainerGracefulShutdownChecker.decreaseRequestCount();
        }
    }
}
