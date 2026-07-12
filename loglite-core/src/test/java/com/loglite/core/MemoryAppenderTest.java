package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MemoryAppender}.
 */
public class MemoryAppenderTest {

    @Test
    public void testCapturesAppendedEvents() {
        MemoryAppender appender = new MemoryAppender();
        LogEvent event = eventWithMessage(LogLevel.INFO, "hello");

        appender.append(event);

        assertEquals(1, appender.getEvents().size());
        assertSame(event, appender.getEvents().get(0));
    }

    @Test
    public void testContainsByMessage() {
        MemoryAppender appender = new MemoryAppender();
        appender.append(eventWithMessage(LogLevel.INFO, "hello"));

        assertTrue(appender.contains("hello"));
        assertFalse(appender.contains("goodbye"));
    }

    @Test
    public void testContainsByLevelAndMessage() {
        MemoryAppender appender = new MemoryAppender();
        appender.append(eventWithMessage(LogLevel.WARN, "careful"));

        assertTrue(appender.contains(LogLevel.WARN, "careful"));
        assertFalse(appender.contains(LogLevel.ERROR, "careful"));
    }

    @Test
    public void testClearRemovesAllEvents() {
        MemoryAppender appender = new MemoryAppender();
        appender.append(eventWithMessage(LogLevel.INFO, "hello"));

        appender.clear();

        assertTrue(appender.getEvents().isEmpty());
    }

    private LogEvent eventWithMessage(LogLevel level, String message) {
        return LogEvent.builder().timestamp(Instant.now()).level(level).message(message).build();
    }
}
