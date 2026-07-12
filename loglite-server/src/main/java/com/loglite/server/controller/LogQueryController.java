package com.loglite.server.controller;

import com.loglite.core.LogLevel;
import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import com.loglite.server.repository.LogEntrySpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Query endpoint for retrieving ingested log entries.
 */
@RestController
@RequestMapping("/api/v1/logs")
public class LogQueryController {

    private static final long DEFAULT_WINDOW_HOURS = 24;
    private static final String METADATA_PARAM_PREFIX = "metadata.";

    private final LogEntryRepository repository;

    public LogQueryController(LogEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<LogEntry> query(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String logger,
            @RequestParam(required = false) List<LogLevel> level,
            @RequestParam Map<String, String> allParams) {

        Instant effectiveTo = to != null ? to : Instant.now();
        Instant effectiveFrom = from != null ? from : effectiveTo.minus(DEFAULT_WINDOW_HOURS, ChronoUnit.HOURS);

        Specification<LogEntry> spec = LogEntrySpecifications.timestampBetween(effectiveFrom, effectiveTo);

        if (logger != null && !logger.isBlank()) {
            spec = spec.and(LogEntrySpecifications.loggerNameMatches(logger));
        }
        if (level != null && !level.isEmpty()) {
            spec = spec.and(LogEntrySpecifications.levelIn(level));
        }

        for (Map.Entry<String, String> param : allParams.entrySet()) {
            if (param.getKey().startsWith(METADATA_PARAM_PREFIX)) {
                String metadataKey = param.getKey().substring(METADATA_PARAM_PREFIX.length());
                spec = spec.and(LogEntrySpecifications.metadataEquals(metadataKey, param.getValue()));
            }
        }

        return repository.findAll(spec);
    }
}
