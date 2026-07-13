package com.loglite.cli.output;

import com.loglite.cli.model.LogEntryDto;

import java.util.List;

/**
 * Renders log entries as a Markdown table.
 */
public final class MarkdownFormatter {

    private MarkdownFormatter() {
    }

    public static String format(List<LogEntryDto> entries) {
        StringBuilder md = new StringBuilder();
        md.append("| Timestamp | Level | Logger | Message |\n");
        md.append("| --- | --- | --- | --- |\n");
        for (LogEntryDto entry : entries) {
            md.append("| ").append(escape(String.valueOf(entry.timestamp())))
                    .append(" | ").append(escape(entry.level()))
                    .append(" | ").append(escape(entry.loggerName()))
                    .append(" | ").append(escape(entry.message()))
                    .append(" |\n");
        }
        return md.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "\\|").replace("\n", " ");
    }
}
