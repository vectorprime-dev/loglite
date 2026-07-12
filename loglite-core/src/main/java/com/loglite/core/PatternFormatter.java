package com.loglite.core;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Formats {@link LogEvent} instances using a pattern string, e.g. {@code "%d [%t] %p %c - %m%n"}.
 * Supported conversion characters:
 * <ul>
 *     <li>{@code %d} - date-time</li>
 *     <li>{@code %t} - thread name</li>
 *     <li>{@code %p} - log level (priority)</li>
 *     <li>{@code %c} - logger category name</li>
 *     <li>{@code %m} - message</li>
 *     <li>{@code %n} - platform line separator</li>
 * </ul>
 */
public class PatternFormatter implements Formatter {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private final String pattern;

    public PatternFormatter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public String format(LogEvent event) {
        StringBuilder result = new StringBuilder(pattern.length() + 32);
        int i = 0;
        while (i < pattern.length()) {
            char c = pattern.charAt(i);
            if (c == '%' && i + 1 < pattern.length()) {
                char conversion = pattern.charAt(i + 1);
                String replacement = resolve(conversion, event);
                if (replacement != null) {
                    result.append(replacement);
                    i += 2;
                    continue;
                }
            }
            result.append(c);
            i++;
        }
        return result.toString();
    }

    private String resolve(char conversion, LogEvent event) {
        return switch (conversion) {
            case 'd' -> TIMESTAMP_FORMAT.format(event.timestamp());
            case 't' -> event.threadName() == null ? "" : event.threadName();
            case 'p' -> event.level().toString();
            case 'c' -> event.loggerName() == null ? "" : event.loggerName();
            case 'm' -> event.message() == null ? "" : event.message();
            case 'n' -> System.lineSeparator();
            default -> null;
        };
    }
}
