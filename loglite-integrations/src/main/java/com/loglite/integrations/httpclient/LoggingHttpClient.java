package com.loglite.integrations.httpclient;

import com.loglite.core.LogManager;
import com.loglite.core.Logger;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Wraps {@link HttpClient} to log each outgoing request's URI, response status, and
 * execution time, and to propagate the current correlation id via {@link CorrelationPropagation}.
 */
public class LoggingHttpClient {

    private final Logger logger = LogManager.getLogger(LoggingHttpClient.class);
    private final HttpClient delegate;

    public LoggingHttpClient(HttpClient delegate) {
        this.delegate = delegate;
    }

    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
        long start = System.nanoTime();
        try {
            HttpResponse<T> response = delegate.send(request, responseBodyHandler);
            log(request, response.statusCode(), start, null);
            return response;
        } catch (IOException | InterruptedException e) {
            log(request, -1, start, e);
            throw e;
        }
    }

    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        long start = System.nanoTime();
        return delegate.sendAsync(request, responseBodyHandler)
                .whenComplete((response, throwable) -> {
                    int status = response != null ? response.statusCode() : -1;
                    log(request, status, start, throwable);
                });
    }

    private void log(HttpRequest request, int status, long startNanos, Throwable error) {
        long durationMillis = (System.nanoTime() - startNanos) / 1_000_000;
        if (error != null) {
            logger.warn("{} {} -> failed after {} ms: {}", request.method(), request.uri(), durationMillis, error.getMessage());
        } else {
            logger.info("{} {} -> {} ({} ms)", request.method(), request.uri(), status, durationMillis);
        }
    }

    /**
     * @param request the request to prepare
     * @return a builder pre-populated with the current correlation id header, ready to be sent
     */
    public static HttpRequest.Builder prepare(HttpRequest.Builder request) {
        return CorrelationPropagation.withCorrelationId(request);
    }
}
