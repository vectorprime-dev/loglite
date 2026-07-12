package com.loglite.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link LogLevel} enum.
 */
public class LogLevelTest {

    @Test
    public void testLogLevelSeveritiesAreOrdered() {
        assertTrue(LogLevel.TRACE.getSeverity() < LogLevel.DEBUG.getSeverity());
        assertTrue(LogLevel.DEBUG.getSeverity() < LogLevel.INFO.getSeverity());
        assertTrue(LogLevel.INFO.getSeverity() < LogLevel.WARN.getSeverity());
        assertTrue(LogLevel.WARN.getSeverity() < LogLevel.ERROR.getSeverity());
        assertTrue(LogLevel.ERROR.getSeverity() < LogLevel.FATAL.getSeverity());
    }

    @Test
    public void testIsEnabledWithInfoConfigured() {
        LogLevel configured = LogLevel.INFO;

        assertFalse(LogLevel.TRACE.isEnabled(configured), "TRACE should be disabled when configured to INFO");
        assertFalse(LogLevel.DEBUG.isEnabled(configured), "DEBUG should be disabled when configured to INFO");
        assertTrue(LogLevel.INFO.isEnabled(configured), "INFO should be enabled when configured to INFO");
        assertTrue(LogLevel.WARN.isEnabled(configured), "WARN should be enabled when configured to INFO");
        assertTrue(LogLevel.ERROR.isEnabled(configured), "ERROR should be enabled when configured to INFO");
        assertTrue(LogLevel.FATAL.isEnabled(configured), "FATAL should be enabled when configured to INFO");
    }

    @Test
    public void testIsEnabledWithFatalConfigured() {
        LogLevel configured = LogLevel.FATAL;

        assertFalse(LogLevel.TRACE.isEnabled(configured));
        assertFalse(LogLevel.DEBUG.isEnabled(configured));
        assertFalse(LogLevel.INFO.isEnabled(configured));
        assertFalse(LogLevel.WARN.isEnabled(configured));
        assertFalse(LogLevel.ERROR.isEnabled(configured));
        assertTrue(LogLevel.FATAL.isEnabled(configured));
    }

    @Test
    public void testIsEnabledNullConfigured() {
        // Default behavior should be true
        assertTrue(LogLevel.TRACE.isEnabled(null));
        assertTrue(LogLevel.INFO.isEnabled(null));
    }
}
