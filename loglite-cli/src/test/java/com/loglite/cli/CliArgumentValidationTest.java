package com.loglite.cli;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Verifies that CLI commands reject invalid arguments with a non-zero exit code and a
 * useful message on stderr, rather than failing with an unhandled exception.
 */
class CliArgumentValidationTest {

    @Test
    void parseCommandRequiresFileOption() {
        CommandLine cli = new CommandLine(new LogliteCli());
        StringWriter err = new StringWriter();
        cli.setErr(new PrintWriter(err));

        int exitCode = cli.execute("parse");

        assertNotEquals(0, exitCode);
        assertTrue(err.toString().toLowerCase().contains("file"), "Expected error to mention missing --file option");
    }

    @Test
    void queryCommandRejectsNonPositiveLimit() {
        CommandLine cli = new CommandLine(new LogliteCli());
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        cli.setOut(new PrintWriter(out));
        cli.setErr(new PrintWriter(err));

        int exitCode = cli.execute("query", "--limit", "0");

        assertEquals(1, exitCode);
    }

    @Test
    void queryCommandRejectsNegativeOffset() {
        CommandLine cli = new CommandLine(new LogliteCli());
        StringWriter out = new StringWriter();
        StringWriter err = new StringWriter();
        cli.setOut(new PrintWriter(out));
        cli.setErr(new PrintWriter(err));

        int exitCode = cli.execute("query", "--offset", "-1");

        assertEquals(1, exitCode);
    }

    @Test
    void queryCommandRejectsNonIntegerLimit() {
        CommandLine cli = new CommandLine(new LogliteCli());
        StringWriter err = new StringWriter();
        cli.setErr(new PrintWriter(err));

        int exitCode = cli.execute("query", "--limit", "not-a-number");

        assertNotEquals(0, exitCode);
    }

    @Test
    void watchCommandRequiresFileOption() {
        CommandLine cli = new CommandLine(new LogliteCli());
        StringWriter err = new StringWriter();
        cli.setErr(new PrintWriter(err));

        int exitCode = cli.execute("watch");

        assertNotEquals(0, exitCode);
        assertTrue(err.toString().toLowerCase().contains("file"), "Expected error to mention missing --file option");
    }
}
