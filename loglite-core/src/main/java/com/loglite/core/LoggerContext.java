package com.loglite.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Holds the runtime configuration (level and appenders) shared by loggers, and
 * maintains the registry of loggers created for each distinct name.
 */
public final class LoggerContext {

    private static final LoggerContext DEFAULT = new LoggerContext(LogLevel.INFO, List.of());

    private final ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Appender> appenders;
    private final LogLevel level;

    LoggerContext(LogLevel level, List<Appender> appenders) {
        this.level = level;
        this.appenders = new CopyOnWriteArrayList<>(appenders);
    }

    public static LoggerContext getInstance() {
        return DEFAULT;
    }

    /**
     * @param name the logger name
     * @return the existing logger registered for this name, or a newly created one
     */
    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, n -> new LogliteLogger(n, level, appenders));
    }

    /**
     * Registers an appender that all loggers obtained from this context will dispatch to,
     * including loggers already created.
     */
    public void addAppender(Appender appender) {
        appenders.add(appender);
    }

    /**
     * @return a new {@link Builder} for configuring a standalone LoggerContext instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Fluent builder for constructing independently configured {@link LoggerContext} instances,
     * e.g. {@code LoggerContext.builder().level(INFO).appender(consoleAppender).build()}.
     */
    public static final class Builder {
        private LogLevel level = LogLevel.INFO;
        private final List<Appender> appenders = new ArrayList<>();

        public Builder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public Builder appender(Appender appender) {
            this.appenders.add(appender);
            return this;
        }

        public LoggerContext build() {
            return new LoggerContext(level, appenders);
        }
    }
}
