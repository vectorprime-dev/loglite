package com.loglite.server.retention;

import com.loglite.server.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Periodically purges log entries older than the configured retention window.
 */
@Component
public class LogRetentionCleanupJob {

    private final LogEntryRepository repository;
    private final int retentionDays;

    public LogRetentionCleanupJob(LogEntryRepository repository,
                                   @Value("${loglite.retention.days:30}") int retentionDays) {
        this.repository = repository;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${loglite.retention.cron:0 0 0 * * *}")
    @Transactional
    public void purgeExpiredLogs() {
        Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
        repository.deleteByTimestampBefore(cutoff);
    }
}
