package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConsoleColorFormatter}.
 */
public class ConsoleColorFormatterTest {

    private final ConsoleColorFormatter formatter = new ConsoleColorFormatter(LogEvent::message);
    private static final String ESC = String.valueOf((char) 27);

    @Test
    public void testErrorIsRed() {
        String formatted = formatter.format(eventAt(LogLevel.ERROR));
        assertTrue(formatted.startsWith(ESC + "[31m"));
    }

    @Test
    public void testWarnIsYellow() {
        String formatted = formatter.format(eventAt(LogLevel.WARN));
        assertTrue(formatted.startsWith(ESC + "[33m"));
    }

    @Test
    public void testInfoIsGreen() {
        String formatted = formatter.format(eventAt(LogLevel.INFO));
        assertTrue(formatted.startsWith(ESC + "[32m"));
    }

    @Test
    public void testOutputEndsWithReset() {
        String formatted = formatter.format(eventAt(LogLevel.INFO));
        assertTrue(formatted.endsWith(ESC + "[0m"));
    }

    private LogEvent eventAt(LogLevel level) {
        return LogEvent.builder().timestamp(Instant.now()).level(level).message("msg").build();
    }
}
