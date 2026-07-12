package com.loglite.core;

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
}
