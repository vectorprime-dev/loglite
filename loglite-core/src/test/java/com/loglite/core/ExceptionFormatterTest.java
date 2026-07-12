package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ExceptionFormatter}.
 */
public class ExceptionFormatterTest {

    @Test
    public void testFormatsMessageAndStackTrace() {
        ExceptionFormatter formatter = new ExceptionFormatter();
        RuntimeException ex = new RuntimeException("boom");

        String formatted = formatter.format(ex);

        assertTrue(formatted.startsWith("java.lang.RuntimeException: boom"));
        assertTrue(formatted.contains("\tat "));
    }

    @Test
    public void testLimitsStackTraceLineCount() {
        ExceptionFormatter formatter = new ExceptionFormatter(1, List.of());
        RuntimeException ex = new RuntimeException("boom");
        assumeAtLeastTwoFrames(ex);

        String formatted = formatter.format(ex);

        long frameLines = formatted.lines().filter(l -> l.startsWith("\tat ")).count();
        assertEquals(1, frameLines);
        assertTrue(formatted.contains("more"));
    }

    @Test
    public void testSanitizesMatchingPackagePrefixes() {
        ExceptionFormatter formatter = new ExceptionFormatter(Integer.MAX_VALUE, List.of("com.loglite.core"));
        RuntimeException ex = new RuntimeException("boom");

        String formatted = formatter.format(ex);

        assertTrue(formatted.contains("REDACTED"));
        assertFalse(formatted.contains("com.loglite.core.ExceptionFormatterTest"));
    }

    @Test
    public void testNullThrowableReturnsEmptyString() {
        ExceptionFormatter formatter = new ExceptionFormatter();
        assertEquals("", formatter.format(null));
    }

    private void assumeAtLeastTwoFrames(Throwable throwable) {
        if (throwable.getStackTrace().length < 2) {
            throw new IllegalStateException("test requires at least 2 stack frames");
        }
    }
}
