package com.loglite.core;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable representation of a single logging event.
 */
public record LogEvent(
        Instant timestamp,
        String loggerName,
        LogLevel level,
        String message,
        String threadName,
        Throwable throwable,
        Map<String, String> mdc) {

    public LogEvent {
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(level, "level must not be null");
        mdc = mdc == null ? Map.of() : Map.copyOf(mdc);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link LogEvent}.
     */
    public static final class Builder {
        private Instant timestamp = Instant.now();
        private String loggerName;
        private LogLevel level;
        private String message;
        private String threadName = Thread.currentThread().getName();
        private Throwable throwable;
        private Map<String, String> mdc = new HashMap<>();

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder loggerName(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder threadName(String threadName) {
            this.threadName = threadName;
            return this;
        }

        public Builder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public Builder mdc(Map<String, String> mdc) {
            this.mdc = mdc == null ? new HashMap<>() : new HashMap<>(mdc);
            return this;
        }

        public LogEvent build() {
            return new LogEvent(timestamp, loggerName, level, message, threadName, throwable, mdc);
        }
    }
}
