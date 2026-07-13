package com.loglite.server.controller;

import com.loglite.core.LogLevel;
import com.loglite.server.dto.PagedResponse;
import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import com.loglite.server.repository.LogEntrySpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final String DEFAULT_SORT_BY = "timestamp";
    private static final String TRACE_ID_METADATA_KEY = "traceId";

    private final LogEntryRepository repository;

    public LogQueryController(LogEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public PagedResponse<LogEntry> query(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @RequestParam(required = false) String logger,
            @RequestParam(required = false) List<LogLevel> level,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = DEFAULT_SORT_BY) String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction sortOrder,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "50") int size,
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
        if (search != null && !search.isBlank()) {
            spec = spec.and(LogEntrySpecifications.messageContains(search));
        }

        for (Map.Entry<String, String> param : allParams.entrySet()) {
            if (param.getKey().startsWith(METADATA_PARAM_PREFIX)) {
                String metadataKey = param.getKey().substring(METADATA_PARAM_PREFIX.length());
                spec = spec.and(LogEntrySpecifications.metadataEquals(metadataKey, param.getValue()));
            }
        }

        String sortProperty = "level".equals(sortBy) ? "level" : DEFAULT_SORT_BY;
        Sort sort = Sort.by(sortOrder, sortProperty);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<LogEntry> result = repository.findAll(spec, pageRequest);
        return PagedResponse.from(result);
    }

    /**
     * @return the distinct logger/service names that have reported log entries
     */
    @GetMapping("/services")
    public List<String> services() {
        return repository.findDistinctLoggerNames();
    }

    /**
     * @param traceId the correlation/trace id to match against each entry's metadata
     * @return all log entries whose metadata carries this trace id
     */
    @GetMapping("/trace/{traceId}")
    public List<LogEntry> byTraceId(@PathVariable String traceId) {
        return repository.findByMetadataKeyValue(TRACE_ID_METADATA_KEY, traceId);
    }
}
