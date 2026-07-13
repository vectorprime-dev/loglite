package com.loglite.cli.output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Wraps regions of text matching a search pattern with ANSI highlight escape codes.
 */
public final class Highlighter {

    private Highlighter() {
    }

    public static String highlight(String text, Pattern pattern) {
        if (text == null || pattern == null) {
            return text;
        }
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.end() == matcher.start()) {
                break;
            }
            result.append(text, lastEnd, matcher.start());
            result.append(AnsiColors.HIGHLIGHT).append(text, matcher.start(), matcher.end()).append(AnsiColors.RESET);
            lastEnd = matcher.end();
        }
        result.append(text.substring(lastEnd));
        return result.toString();
    }
}
