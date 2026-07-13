package com.loglite.cli.output;

import com.loglite.cli.model.LogEntryDto;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Renders log entries as {@code [LEVEL] HH:mm:ss [Logger] Message}, with any
 * embedded multi-line stack trace indented beneath the summary line.
 */
public final class PrettyFormatter {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private PrettyFormatter() {
    }

    public static String format(LogEntryDto entry) {
        return format(entry, false);
    }

    public static String format(LogEntryDto entry, boolean color) {
        String time = entry.timestamp() != null ? TIME_FORMAT.format(entry.timestamp()) : "--:--:--";
        String[] lines = entry.message() != null ? entry.message().split("\n", -1) : new String[] {""};

        String levelTag = "[" + entry.level() + "]";
        if (color) {
            levelTag = AnsiColors.colorFor(entry.level()) + levelTag + AnsiColors.RESET;
        }

        StringBuilder result = new StringBuilder();
        result.append(levelTag).append(' ')
                .append(time).append(" [").append(entry.loggerName()).append("] ")
                .append(lines[0]);

        for (int i = 1; i < lines.length; i++) {
            result.append('\n').append('\t').append(lines[i]);
        }

        return result.toString();
    }
}
