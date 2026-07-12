package com.loglite.server.controller;

import com.loglite.server.repository.LogEntryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Exposes aggregate statistics over ingested log entries.
 */
@RestController
@RequestMapping("/api/v1/logs/stats")
public class LogStatsController {

    private static final Set<String> SUPPORTED_INTERVALS = Set.of("minute", "hour");

    private final LogEntryRepository repository;

    public LogStatsController(LogEntryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/levels")
    public Map<String, Long> countsByLevel() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (LogEntryRepository.LevelCount levelCount : repository.countGroupedByLevel()) {
            counts.put(levelCount.getLevel().name(), levelCount.getCount());
        }
        return counts;
    }

    @GetMapping("/timeline")
    public Map<String, Long> timeline(@RequestParam(required = false, defaultValue = "hour") String interval) {
        String bucket = SUPPORTED_INTERVALS.contains(interval) ? interval : "hour";

        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : repository.countGroupedByTimeBucket(bucket)) {
            String bucketStart = ((Timestamp) row[0]).toInstant().toString();
            long count = ((Number) row[1]).longValue();
            counts.put(bucketStart, count);
        }
        return counts;
    }
}
