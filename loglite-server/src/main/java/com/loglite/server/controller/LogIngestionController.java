package com.loglite.server.controller;

import com.loglite.server.dto.BulkIngestResponse;
import com.loglite.server.dto.LogIngestRequest;
import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Accepts log events for ingestion into the backing store.
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
        LogEntry saved = repository.save(toEntry(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/bulk")
    @Transactional
    public ResponseEntity<BulkIngestResponse> ingestBulk(@RequestBody List<LogIngestRequest> requests) {
        List<LogEntry> entries = requests.stream().map(this::toEntry).toList();
        List<LogEntry> saved = repository.saveAll(entries);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BulkIngestResponse(saved.size(), "OK"));
    }

    private LogEntry toEntry(LogIngestRequest request) {
        return new LogEntry(
                UUID.randomUUID(),
                request.timestamp() != null ? request.timestamp() : Instant.now(),
                request.loggerName(),
                request.level(),
                request.message(),
                request.threadName(),
                request.metadata());
    }
}
