package com.loglite.server.dto;

import com.loglite.core.LogLevel;

import java.time.Instant;
import java.util.Map;

/**
 * JSON payload accepted by the log ingestion endpoint, representing a single log event.
 */
public record LogIngestRequest(
        Instant timestamp,
        String loggerName,
        LogLevel level,
        String message,
        String threadName,
        Map<String, String> metadata) {
}
