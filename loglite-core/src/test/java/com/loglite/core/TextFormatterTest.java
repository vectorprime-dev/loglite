package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TextFormatter}.
 */
public class TextFormatterTest {

    private final TextFormatter formatter = new TextFormatter();

    @Test
    public void testFormatsStandardFields() {
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .loggerName("com.example.Foo")
                .level(LogLevel.INFO)
                .message("hello world")
                .threadName("main")
                .build();

        String formatted = formatter.format(event);

        assertTrue(formatted.contains("[INFO]"));
        assertTrue(formatted.contains("[main]"));
        assertTrue(formatted.contains("com.example.Foo - hello world"));
    }

    @Test
    public void testHandlesEmptyLoggerName() {
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .loggerName(null)
                .level(LogLevel.WARN)
                .message("m")
                .threadName("t")
                .build();

        String formatted = formatter.format(event);

        assertTrue(formatted.contains(" - m"));
    }

    @Test
    public void testIncludesStackTraceWhenExceptionPresent() {
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .loggerName("logger")
                .level(LogLevel.ERROR)
                .message("failed")
                .threadName("t")
                .throwable(new RuntimeException("boom"))
                .build();

        String formatted = formatter.format(event);

        assertTrue(formatted.contains("failed"));
        assertTrue(formatted.contains("java.lang.RuntimeException: boom"));
    }
}
