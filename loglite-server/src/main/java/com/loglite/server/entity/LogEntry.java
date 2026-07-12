package com.loglite.server.entity;

import com.loglite.core.LogLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity mapping a single ingested log event to the {@code log_entries} table.
 */
@Entity
@Table(name = "log_entries")
public class LogEntry {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Instant timestamp;

    private String loggerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    @Column(length = 8192)
    private String message;

    private String threadName;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> metadata;

    protected LogEntry() {
        // required by JPA
    }

    public LogEntry(UUID id, Instant timestamp, String loggerName, LogLevel level, String message,
                    String threadName, Map<String, String> metadata) {
        this.id = id;
        this.timestamp = timestamp;
        this.loggerName = loggerName;
        this.level = level;
        this.message = message;
        this.threadName = threadName;
        this.metadata = metadata;
    }

    public UUID getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getThreadName() {
        return threadName;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}
