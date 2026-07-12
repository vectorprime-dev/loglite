package com.loglite.core;

/**
 * Discards events whose severity is below a configured minimum level.
 */
public class ThresholdFilter implements LogFilter {

    private final LogLevel minimumLevel;

    public ThresholdFilter(LogLevel minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    @Override
    public boolean filter(LogEvent event) {
        return event.level().isEnabled(minimumLevel);
    }
}
