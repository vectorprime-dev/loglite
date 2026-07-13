package com.loglite.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Non-blocking Logback appender that batches events and posts them as JSON to a
 * {@code loglite-server} instance's bulk ingestion endpoint.
 */
public class LogliteHttpAppender extends AppenderBase<ILoggingEvent> {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Queue<ILoggingEvent> queue = new ConcurrentLinkedQueue<>();
    private HttpClient httpClient;
    private ScheduledExecutorService scheduler;

    private String url;
    private String apiKey;
    private int batchSize = 100;
    private long flushIntervalMillis = 1000;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setFlushIntervalMillis(long flushIntervalMillis) {
        this.flushIntervalMillis = flushIntervalMillis;
    }

    @Override
    public void start() {
        if (url == null || url.isBlank()) {
            addError("loglite url is not configured; appender will not start");
            return;
        }
        httpClient = HttpClient.newHttpClient();
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "loglite-appender-flush");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(this::flush, flushIntervalMillis, flushIntervalMillis, TimeUnit.MILLISECONDS);
        super.start();
    }

    @Override
    public void stop() {
        flush();
        if (scheduler != null) {
            scheduler.shutdown();
        }
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {
        queue.add(event);
        if (queue.size() >= batchSize) {
            flush();
        }
    }

    private void flush() {
        List<ILoggingEvent> batch = drain();
        if (batch.isEmpty()) {
            return;
        }
        try {
            List<Map<String, Object>> payload = batch.stream().map(this::toPayload).toList();
            String json = MAPPER.writeValueAsString(payload);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url + "/api/v1/logs/bulk"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));
            if (apiKey != null && !apiKey.isBlank()) {
                requestBuilder.header("X-API-Key", apiKey);
            }

            HttpResponse<Void> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() / 100 != 2) {
                addError("loglite server responded with HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            addError("Failed to forward log batch to loglite server", e);
        }
    }

    private List<ILoggingEvent> drain() {
        List<ILoggingEvent> batch = new ArrayList<>();
        ILoggingEvent event;
        while ((event = queue.poll()) != null) {
            batch.add(event);
        }
        return batch;
    }

    private Map<String, Object> toPayload(ILoggingEvent event) {
        return Map.of(
                "timestamp", Instant.ofEpochMilli(event.getTimeStamp()).toString(),
                "loggerName", event.getLoggerName(),
                "level", event.getLevel().toString(),
                "message", event.getFormattedMessage(),
                "threadName", event.getThreadName(),
                "metadata", event.getMDCPropertyMap() != null ? event.getMDCPropertyMap() : Map.of());
    }
}
