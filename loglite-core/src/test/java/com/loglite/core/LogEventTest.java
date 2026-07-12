package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link LogEvent} record.
 */
public class LogEventTest {

    @Test
    public void testBuilderCreatesEventWithExpectedFields() {
        Instant now = Instant.now();
        LogEvent event = LogEvent.builder()
                .timestamp(now)
                .loggerName("com.example.Foo")
                .level(LogLevel.INFO)
                .message("hello")
                .threadName("main")
                .build();

        assertEquals(now, event.timestamp());
        assertEquals("com.example.Foo", event.loggerName());
        assertEquals(LogLevel.INFO, event.level());
        assertEquals("hello", event.message());
        assertEquals("main", event.threadName());
        assertTrue(event.mdc().isEmpty());
    }

    @Test
    public void testMdcIsDefensivelyCopied() {
        Map<String, String> mdc = new HashMap<>();
        mdc.put("userId", "123");

        LogEvent event = LogEvent.builder()
                .level(LogLevel.WARN)
                .message("m")
                .mdc(mdc)
                .build();

        mdc.put("userId", "456");

        assertEquals("123", event.mdc().get("userId"));
        assertThrows(UnsupportedOperationException.class, () -> event.mdc().put("x", "y"));
    }

    @Test
    public void testNullMdcDefaultsToEmptyMap() {
        LogEvent event = new LogEvent(Instant.now(), "logger", LogLevel.ERROR, "msg", "t", null, null);
        assertNotNull(event.mdc());
        assertTrue(event.mdc().isEmpty());
    }
}
