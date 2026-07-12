package com.loglite.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConsoleAppender}.
 */
public class ConsoleAppenderTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeEach
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testInfoLevelWritesToStdOut() {
        ConsoleAppender appender = new ConsoleAppender(LogEvent::message);
        LogEvent event = LogEvent.builder().timestamp(Instant.now()).level(LogLevel.INFO).message("hello").build();

        appender.append(event);

        assertTrue(outContent.toString().contains("hello"));
        assertTrue(errContent.toString().isEmpty());
    }

    @Test
    public void testErrorLevelWritesToStdErr() {
        ConsoleAppender appender = new ConsoleAppender(LogEvent::message);
        LogEvent event = LogEvent.builder().timestamp(Instant.now()).level(LogLevel.ERROR).message("boom").build();

        appender.append(event);

        assertTrue(errContent.toString().contains("boom"));
        assertTrue(outContent.toString().isEmpty());
    }
}
