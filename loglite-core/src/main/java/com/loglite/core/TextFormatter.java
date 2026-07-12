package com.loglite.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Default text layout: {@code [YYYY-MM-DD HH:mm:ss.SSS] [LEVEL] [Thread] LoggerName - Message}.
 */
public class TextFormatter implements Formatter {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    @Override
    public String format(LogEvent event) {
        String loggerName = event.loggerName() == null ? "" : event.loggerName();
        String threadName = event.threadName() == null ? "" : event.threadName();
        String message = event.message() == null ? "" : event.message();

        StringBuilder builder = new StringBuilder();
        builder.append('[').append(TIMESTAMP_FORMAT.format(event.timestamp())).append(']')
                .append(" [").append(event.level()).append(']')
                .append(" [").append(threadName).append(']')
                .append(' ').append(loggerName)
                .append(" - ").append(message);

        if (event.throwable() != null) {
            builder.append(System.lineSeparator()).append(stackTraceOf(event.throwable()));
        }

        return builder.toString();
    }

    private String stackTraceOf(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString().stripTrailing();
    }
}
