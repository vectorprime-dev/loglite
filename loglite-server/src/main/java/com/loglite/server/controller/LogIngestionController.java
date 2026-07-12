package com.loglite.server.controller;

import com.loglite.server.dto.LogIngestRequest;
import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

/**
 * Accepts single log events for ingestion into the backing store.
 */
@RestController
@RequestMapping("/api/v1/logs")
public class LogIngestionController {

    private final LogEntryRepository repository;

    public LogIngestionController(LogEntryRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<LogEntry> ingest(@RequestBody LogIngestRequest request) {
        LogEntry entry = new LogEntry(
                UUID.randomUUID(),
                request.timestamp() != null ? request.timestamp() : Instant.now(),
                request.loggerName(),
                request.level(),
                request.message(),
                request.threadName(),
                request.metadata());

        LogEntry saved = repository.save(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
