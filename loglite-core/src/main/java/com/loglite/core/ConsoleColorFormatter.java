package com.loglite.core;

/**
 * Wraps another {@link Formatter} and colorizes its output using ANSI escape codes based on level:
 * red for ERROR/FATAL, yellow for WARN, green for INFO, blue for DEBUG, white for TRACE.
 */
public class ConsoleColorFormatter implements Formatter {

    private static final String ESC = String.valueOf((char) 27);
    private static final String RESET = ESC + "[0m";
    private static final String RED = ESC + "[31m";
    private static final String YELLOW = ESC + "[33m";
    private static final String GREEN = ESC + "[32m";
    private static final String BLUE = ESC + "[34m";
    private static final String WHITE = ESC + "[37m";

    private final Formatter delegate;

    public ConsoleColorFormatter() {
        this(new TextFormatter());
    }

    public ConsoleColorFormatter(Formatter delegate) {
        this.delegate = delegate;
    }

    @Override
    public String format(LogEvent event) {
        return colorFor(event.level()) + delegate.format(event) + RESET;
    }

    private String colorFor(LogLevel level) {
        return switch (level) {
            case ERROR, FATAL -> RED;
            case WARN -> YELLOW;
            case INFO -> GREEN;
            case DEBUG -> BLUE;
            case TRACE -> WHITE;
        };
    }
}
