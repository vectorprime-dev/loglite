package com.loglite.server.controller;

import com.loglite.server.repository.LogEntryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Exposes aggregate statistics over ingested log entries.
 */
@RestController
@RequestMapping("/api/v1/logs/stats")
public class LogStatsController {

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
}
