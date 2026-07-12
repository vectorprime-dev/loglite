package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ThresholdFilter}.
 */
public class ThresholdFilterTest {

    @Test
    public void testDiscardsEventsBelowMinimumLevel() {
        LogFilter filter = new ThresholdFilter(LogLevel.WARN);

        assertFalse(filter.filter(eventAt(LogLevel.DEBUG)));
        assertFalse(filter.filter(eventAt(LogLevel.INFO)));
        assertTrue(filter.filter(eventAt(LogLevel.WARN)));
        assertTrue(filter.filter(eventAt(LogLevel.ERROR)));
    }

    private LogEvent eventAt(LogLevel level) {
        return LogEvent.builder().timestamp(Instant.now()).level(level).message("m").build();
    }
}
