package com.loglite.cli.model;

import java.time.Instant;
import java.util.Map;

/**
 * Client-side representation of a log entry returned by {@code loglite-server}.
 */
public record LogEntryDto(
        String id,
        Instant timestamp,
        String loggerName,
        String level,
        String message,
        String threadName,
        Map<String, String> metadata) {
}
