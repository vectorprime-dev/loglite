package com.loglite.server.controller;

import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String logger) {

        Instant effectiveTo = to != null ? to : Instant.now();
        Instant effectiveFrom = from != null ? from : effectiveTo.minus(DEFAULT_WINDOW_HOURS, ChronoUnit.HOURS);

        Specification<LogEntry> spec = (root, query, cb) ->
                cb.between(root.get("timestamp"), effectiveFrom, effectiveTo);

        if (logger != null && !logger.isBlank()) {
            spec = spec.and(loggerNameMatches(logger));
        }

        return repository.findAll(spec);
    }

    /**
     * Matches entries whose logger name exactly equals, or starts with, any of the
     * comma-separated candidate names.
     */
    private Specification<LogEntry> loggerNameMatches(String commaSeparatedNames) {
        List<String> names = Arrays.stream(commaSeparatedNames.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .toList();

        return (root, query, cb) -> {
            List<Predicate> predicates = names.stream()
                    .map(name -> cb.or(
                            cb.equal(root.get("loggerName"), name),
                            cb.like(root.get("loggerName"), name + "%")))
                    .toList();
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
