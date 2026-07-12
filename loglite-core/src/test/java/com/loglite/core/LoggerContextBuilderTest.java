package com.loglite.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LoggerContext.Builder}.
 */
public class LoggerContextBuilderTest {

    @Test
    public void testBuilderConfiguresIndependentContext() {
        MemoryAppender appender = new MemoryAppender();
        LoggerContext context = LoggerContext.builder()
                .level(LogLevel.WARN)
                .appender(appender)
                .build();

        Logger logger = context.getLogger("test");
        logger.info("should be filtered out");
        logger.warn("should appear");

        assertEquals(1, appender.getEvents().size());
        assertTrue(appender.contains("should appear"));
    }

    @Test
    public void testBuiltContextIsIndependentOfDefault() {
        LoggerContext custom = LoggerContext.builder().level(LogLevel.ERROR).build();
        assertNotSame(LoggerContext.getInstance(), custom);
    }
}
