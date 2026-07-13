package com.loglite.integrations.servlet;

import com.loglite.core.LogManager;
import com.loglite.core.Logger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Standard {@code jakarta.servlet.Filter} that logs a one-line summary of each inbound
 * HTTP request: method, URI, response status, and duration.
 */
public class RequestLoggingFilter implements Filter {

    private final Logger logger = LogManager.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }

        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long durationMillis = (System.nanoTime() - start) / 1_000_000;
            logger.info("{} {} -> {} ({} ms)", httpRequest.getMethod(), httpRequest.getRequestURI(),
                    httpResponse.getStatus(), durationMillis);
        }
    }
}
