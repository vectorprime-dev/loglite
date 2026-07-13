package com.loglite.cli.output;

/**
 * Truncates log messages to fit within a maximum line length, appending an ellipsis.
 */
public final class Truncator {

    private Truncator() {
    }

    public static String truncate(String text, int maxLength) {
        if (text == null || maxLength <= 0 || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }
}
