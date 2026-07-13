package com.loglite.integrations.httpclient;

import com.loglite.core.MDC;

import java.net.http.HttpRequest;

/**
 * Propagates the current {@link MDC} correlation id onto outgoing HTTP requests, so
 * downstream services can join their logs back to the originating request.
 */
public final class CorrelationPropagation {

    public static final String CORRELATION_ID_MDC_KEY = "traceId";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    private CorrelationPropagation() {
    }

    /**
     * Adds the current thread's correlation id as a header on the given request builder,
     * if one is bound in {@link MDC}. No-op otherwise.
     */
    public static HttpRequest.Builder withCorrelationId(HttpRequest.Builder builder) {
        String correlationId = MDC.get(CORRELATION_ID_MDC_KEY);
        if (correlationId != null && !correlationId.isBlank()) {
            builder.header(CORRELATION_ID_HEADER, correlationId);
        }
        return builder;
    }
}
