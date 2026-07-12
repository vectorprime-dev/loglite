package com.loglite.core;

/**
 * Appender that writes formatted log events to standard output or error streams.
 * Events at WARN and below are printed to {@link System#out}; ERROR and FATAL go to {@link System#err}.
 */
public class ConsoleAppender implements Appender {

    private final Formatter formatter;

    public ConsoleAppender(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void append(LogEvent event) {
        String formatted = formatter.format(event);
        if (event.level().getSeverity() >= LogLevel.ERROR.getSeverity()) {
            System.err.println(formatted);
        } else {
            System.out.println(formatted);
        }
    }
}
