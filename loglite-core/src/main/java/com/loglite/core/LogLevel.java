package com.loglite.core;

/**
 * Represents the log severity levels for the Loglite toolkit.
 */
public enum LogLevel {
    TRACE(100),
    DEBUG(200),
    INFO(300),
    WARN(400),
    ERROR(500),
    FATAL(600);

    private final int severity;

    LogLevel(int severity) {
        this.severity = severity;
    }

    /**
     * Gets the numeric severity of this log level.
     * Higher numbers represent higher severity.
     *
     * @return the severity value
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Checks if this log level is enabled given the active configured log level.
     * A log level is enabled if its severity is greater than or equal to the active level.
     * E.g., if active level is INFO, then ERROR.isEnabled(INFO) returns true, while DEBUG.isEnabled(INFO) returns false.
     *
     * @param configuredLevel the active configured logging level
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled(LogLevel configuredLevel) {
        if (configuredLevel == null) {
            return true; // Default fallback to enabled if level is unspecified
        }
        return this.severity >= configuredLevel.getSeverity();
    }
}
