package com.loglite.server.controller;

import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Query endpoint for retrieving ingested log entries.
 */
@RestController
@RequestMapping("/api/v1/logs")
public class LogQueryController {

    private static final long DEFAULT_WINDOW_HOURS = 24;

    private final LogEntryRepository repository;

    public LogQueryController(LogEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<LogEntry> query(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to) {

        Instant effectiveTo = to != null ? to : Instant.now();
        Instant effectiveFrom = from != null ? from : effectiveTo.minus(DEFAULT_WINDOW_HOURS, ChronoUnit.HOURS);

        Specification<LogEntry> spec = (root, query, cb) ->
                cb.between(root.get("timestamp"), effectiveFrom, effectiveTo);

        return repository.findAll(spec);
    }
}
