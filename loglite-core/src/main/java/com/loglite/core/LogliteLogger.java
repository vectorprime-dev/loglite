package com.loglite.core;

import java.time.Instant;
import java.util.List;

/**
 * Standard {@link Logger} implementation that formats parameterized messages,
 * builds {@link LogEvent} instances, and delegates them to configured appenders.
 */
public class LogliteLogger implements Logger {

    private final String name;
    private final LogLevel configuredLevel;
    private final List<Appender> appenders;

    public LogliteLogger(String name, LogLevel configuredLevel, List<Appender> appenders) {
        this.name = name;
        this.configuredLevel = configuredLevel;
        this.appenders = appenders;
    }

    @Override
    public void trace(String message, Object... args) {
        log(LogLevel.TRACE, message, args);
    }

    @Override
    public void debug(String message, Object... args) {
        log(LogLevel.DEBUG, message, args);
    }

    @Override
    public void info(String message, Object... args) {
        log(LogLevel.INFO, message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        log(LogLevel.WARN, message, args);
    }

    @Override
    public void error(String message, Object... args) {
        log(LogLevel.ERROR, message, args);
    }

    @Override
    public void fatal(String message, Object... args) {
        log(LogLevel.FATAL, message, args);
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return level.isEnabled(configuredLevel);
    }

    private void log(LogLevel level, String message, Object[] args) {
        if (!level.isEnabled(configuredLevel)) {
            return;
        }

        Throwable throwable = null;
        int argCount = args.length;
        if (argCount > 0 && args[argCount - 1] instanceof Throwable last) {
            throwable = last;
            argCount--;
        }

        String formatted = formatMessage(message, args, argCount);

        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .loggerName(name)
                .level(level)
                .message(formatted)
                .threadName(Thread.currentThread().getName())
                .throwable(throwable)
                .build();

        for (Appender appender : appenders) {
            appender.append(event);
        }
    }

    private String formatMessage(String message, Object[] args, int argCount) {
        if (message == null || argCount == 0) {
            return message;
        }

        StringBuilder result = new StringBuilder(message.length() + 16);
        int argIndex = 0;
        int i = 0;
        while (i < message.length()) {
            if (argIndex < argCount && i < message.length() - 1
                    && message.charAt(i) == '{' && message.charAt(i + 1) == '}') {
                result.append(args[argIndex++]);
                i += 2;
            } else {
                result.append(message.charAt(i));
                i++;
            }
        }
        return result.toString();
    }
}
