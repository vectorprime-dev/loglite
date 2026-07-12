package com.loglite.server.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rejects requests to the log ingestion/query API that are missing a valid API key header.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String PROTECTED_PATH_PREFIX = "/api/v1/logs";

    private final String expectedApiKey;

    public ApiKeyAuthFilter(@Value("${loglite.security.api-key:changeme}") String expectedApiKey) {
        this.expectedApiKey = expectedApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.getRequestURI().startsWith(PROTECTED_PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);
        if (providedApiKey == null || !providedApiKey.equals(expectedApiKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid API key");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
