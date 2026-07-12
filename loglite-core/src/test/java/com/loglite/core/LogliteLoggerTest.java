package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LogliteLogger}.
 */
public class LogliteLoggerTest {

    @Test
    public void testParameterizedMessageIsFormatted() {
        List<LogEvent> captured = new ArrayList<>();
        Logger logger = new LogliteLogger("test", LogLevel.INFO, List.of(captured::add));

        logger.info("Hello {}, you are {} years old", "Alice", 30);

        assertEquals(1, captured.size());
        assertEquals("Hello Alice, you are 30 years old", captured.get(0).message());
    }

    @Test
    public void testMessagesBelowConfiguredLevelAreDropped() {
        List<LogEvent> captured = new ArrayList<>();
        Logger logger = new LogliteLogger("test", LogLevel.WARN, List.of(captured::add));

        logger.debug("should not appear");
        logger.info("also should not appear");
        logger.warn("this should appear");

        assertEquals(1, captured.size());
        assertEquals("this should appear", captured.get(0).message());
    }

    @Test
    public void testTrailingThrowableIsCapturedSeparately() {
        List<LogEvent> captured = new ArrayList<>();
        Logger logger = new LogliteLogger("test", LogLevel.ERROR, List.of(captured::add));
        RuntimeException ex = new RuntimeException("boom");

        logger.error("failed to process {}", "job-1", ex);

        assertEquals(1, captured.size());
        assertEquals("failed to process job-1", captured.get(0).message());
        assertSame(ex, captured.get(0).throwable());
    }

    @Test
    public void testIsEnabled() {
        Logger logger = new LogliteLogger("test", LogLevel.INFO, List.of());

        assertFalse(logger.isEnabled(LogLevel.DEBUG));
        assertTrue(logger.isEnabled(LogLevel.INFO));
        assertTrue(logger.isEnabled(LogLevel.ERROR));
    }
}
