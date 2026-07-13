package com.loglite.integrations.security;

import com.loglite.core.MDC;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * Binds the current Spring Security principal's username into {@link MDC} for the
 * duration of a request, so downstream log lines automatically carry it.
 */
public class SecurityMdcFilter implements Filter {

    public static final String MDC_KEY = "user";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean bound = false;
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
            MDC.put(MDC_KEY, authentication.getName());
            bound = true;
        }
        try {
            chain.doFilter(request, response);
        } finally {
            if (bound) {
                MDC.remove(MDC_KEY);
            }
        }
    }
}
