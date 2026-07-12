package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PatternFormatter}.
 */
public class PatternFormatterTest {

    @Test
    public void testFormatsAllConversionCharacters() {
        PatternFormatter formatter = new PatternFormatter("%d [%t] %p %c - %m%n");
        LogEvent event = LogEvent.builder()
                .timestamp(Instant.now())
                .loggerName("com.example.Foo")
                .level(LogLevel.WARN)
                .message("hello")
                .threadName("main")
                .build();

        String formatted = formatter.format(event);

        assertTrue(formatted.contains("[main]"));
        assertTrue(formatted.contains("WARN"));
        assertTrue(formatted.contains("com.example.Foo - hello"));
        assertTrue(formatted.endsWith(System.lineSeparator()));
    }

    @Test
    public void testUnknownConversionCharacterIsLeftLiteral() {
        PatternFormatter formatter = new PatternFormatter("%x %m");
        LogEvent event = LogEvent.builder().timestamp(Instant.now()).level(LogLevel.INFO).message("msg").build();

        String formatted = formatter.format(event);

        assertEquals("%x msg", formatted);
    }
}
