package com.loglite.cli.output;

import com.loglite.cli.model.LogEntryDto;

import java.util.List;

/**
 * Renders log entries as CSV, suitable for redirecting to a file.
 */
public final class CsvFormatter {

    private CsvFormatter() {
    }

    public static String format(List<LogEntryDto> entries) {
        StringBuilder csv = new StringBuilder("timestamp,level,logger,thread,message\n");
        for (LogEntryDto entry : entries) {
            csv.append(escape(String.valueOf(entry.timestamp()))).append(',')
                    .append(escape(entry.level())).append(',')
                    .append(escape(entry.loggerName())).append(',')
                    .append(escape(entry.threadName())).append(',')
                    .append(escape(entry.message()))
                    .append('\n');
        }
        return csv.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }
}
