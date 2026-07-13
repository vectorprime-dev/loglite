package com.loglite.cli.output;

import com.loglite.cli.model.LogEntryDto;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        return format(entry, color, null);
    }

    /**
     * @param columns field names to include, in order (any of {@code time}, {@code level},
     *                {@code logger}, {@code msg}, {@code thread}), or null for the default
     *                {@code [LEVEL] HH:mm:ss [Logger] Message} layout
     */
    public static String format(LogEntryDto entry, boolean color, List<String> columns) {
        String time = entry.timestamp() != null ? TIME_FORMAT.format(entry.timestamp()) : "--:--:--";
        String[] lines = entry.message() != null ? entry.message().split("\n", -1) : new String[] {""};

        String levelTag = "[" + entry.level() + "]";
        if (color) {
            levelTag = AnsiColors.colorFor(entry.level()) + levelTag + AnsiColors.RESET;
        }

        StringBuilder result = new StringBuilder();
        if (columns == null) {
            result.append(levelTag).append(' ')
                    .append(time).append(" [").append(entry.loggerName()).append("] ")
                    .append(lines[0]);
        } else {
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    result.append(' ');
                }
                result.append(renderColumn(columns.get(i), entry, time, levelTag, lines[0]));
            }
        }

        for (int i = 1; i < lines.length; i++) {
            result.append('\n').append('\t').append(lines[i]);
        }

        return result.toString();
    }

    private static String renderColumn(String column, LogEntryDto entry, String time, String levelTag, String firstLine) {
        return switch (column.trim().toLowerCase()) {
            case "time" -> time;
            case "level" -> levelTag;
            case "logger" -> "[" + entry.loggerName() + "]";
            case "msg", "message" -> firstLine;
            case "thread" -> String.valueOf(entry.threadName());
            default -> "";
        };
    }
}
