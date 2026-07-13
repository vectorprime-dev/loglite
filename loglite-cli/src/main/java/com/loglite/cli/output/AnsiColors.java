package com.loglite.cli.output;

/**
 * ANSI escape codes for colorizing terminal output by log level.
 */
public final class AnsiColors {

    private static final String ESC = String.valueOf((char) 27);
    public static final String RESET = ESC + "[0m";
    private static final String RED = ESC + "[31m";
    private static final String YELLOW = ESC + "[33m";
    private static final String GREEN = ESC + "[32m";
    private static final String BLUE = ESC + "[34m";
    private static final String WHITE = ESC + "[37m";

    private AnsiColors() {
    }

    public static String colorFor(String level) {
        if (level == null) {
            return WHITE;
        }
        return switch (level.toUpperCase()) {
            case "ERROR", "FATAL" -> RED;
            case "WARN" -> YELLOW;
            case "INFO" -> GREEN;
            case "DEBUG" -> BLUE;
            default -> WHITE;
        };
    }
}
