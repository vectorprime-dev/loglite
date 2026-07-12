package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JsonFormatter}.
 */
public class JsonFormatterTest {

    private final JsonFormatter formatter = new JsonFormatter();

    @Test
    public void testSerializesCoreFields() {
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.parse("2026-01-01T00:00:00Z"))
                .loggerName("com.example.Foo")
                .level(LogLevel.INFO)
                .message("hello")
                .threadName("main")
                .build();

        String json = formatter.format(event);

        assertTrue(json.contains("\"level\":\"INFO\""));
        assertTrue(json.contains("\"thread\":\"main\""));
        assertTrue(json.contains("\"logger\":\"com.example.Foo\""));
        assertTrue(json.contains("\"message\":\"hello\""));
        assertFalse(json.contains("\"mdc\""));
        assertFalse(json.contains("\"exception\""));
    }

    @Test
    public void testIncludesMdcAndExceptionWhenPresent() {
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .level(LogLevel.ERROR)
                .message("failed")
                .mdc(Map.of("userId", "42"))
                .throwable(new RuntimeException("boom"))
                .build();

        String json = formatter.format(event);

        assertTrue(json.contains("\"userId\":\"42\""));
        assertTrue(json.contains("\"exception\""));
        assertTrue(json.contains("RuntimeException"));
    }
}
