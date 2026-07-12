package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LogManager} and {@link LoggerContext}.
 */
public class LogManagerTest {

    @Test
    public void testGetLoggerReturnsSameInstanceForSameName() {
        Logger first = LogManager.getLogger("com.example.Foo");
        Logger second = LogManager.getLogger("com.example.Foo");

        assertSame(first, second);
    }

    @Test
    public void testGetLoggerByClass() {
        Logger logger = LogManager.getLogger(LogManagerTest.class);
        assertNotNull(logger);
    }

    @Test
    public void testAddAppenderAffectsExistingLoggers() {
        List<LogEvent> captured = new ArrayList<>();
        Logger logger = LogManager.getLogger("com.example.DynamicAppenderTest");

        logger.info("before appender registered");
        assertTrue(captured.isEmpty());

        LogManager.addAppender(captured::add);
        logger.info("after appender registered");

        assertEquals(1, captured.size());
        assertEquals("after appender registered", captured.get(0).message());
    }
}
