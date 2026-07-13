package com.loglite.cli.model;

import java.time.Instant;
import java.util.Map;

/**
 * Payload shape accepted by the server's bulk ingestion endpoint (mirrors
 * {@code LogIngestRequest} on the server, minus the server-assigned id).
 */
public record IngestPayload(
        Instant timestamp,
        String loggerName,
        String level,
        String message,
        String threadName,
        Map<String, String> metadata) {

    public static IngestPayload from(LogEntryDto entry) {
        return new IngestPayload(entry.timestamp(), entry.loggerName(), entry.level(), entry.message(),
                entry.threadName(), entry.metadata());
    }
}
