package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cross-cutting checks that every {@link Formatter} layout renders the level and
 * message of a given event, so no layout silently drops core fields.
 */
class LayoutFormatConsistencyTest {

    private static final LogEvent EVENT = LogEvent.builder()
            .timestamp(Instant.parse("2026-01-01T00:00:00Z"))
            .loggerName("com.example.Foo")
            .level(LogLevel.WARN)
            .message("disk usage high")
            .threadName("main")
            .build();

    @Test
    void everyFormatterIncludesLevelAndMessage() {
        List<Formatter> formatters = List.of(
                new JsonFormatter(),
                new TextFormatter(),
                new PatternFormatter("%p %m"),
                new ConsoleColorFormatter(new TextFormatter()));

        for (Formatter formatter : formatters) {
            String output = formatter.format(EVENT);
            assertFalse(output == null || output.isBlank(), formatter.getClass().getSimpleName() + " produced blank output");
            assertTrue(output.contains("WARN"), formatter.getClass().getSimpleName() + " dropped the level");
            assertTrue(output.contains("disk usage high"), formatter.getClass().getSimpleName() + " dropped the message");
        }
    }
}
