package com.loglite.core;

import java.util.List;

/**
 * Formats a {@link Throwable} into a string layout: message followed by stack trace frames.
 */
public class ExceptionFormatter {

    private static final String REDACTED_FRAME = "\tat REDACTED";

    private final int maxLines;
    private final List<String> sanitizedPackagePrefixes;

    public ExceptionFormatter() {
        this(Integer.MAX_VALUE, List.of());
    }

    /**
     * @param maxLines                 maximum number of stack trace lines to include (remaining are summarized)
     * @param sanitizedPackagePrefixes frame class names starting with any of these prefixes are redacted
     */
    public ExceptionFormatter(int maxLines, List<String> sanitizedPackagePrefixes) {
        this.maxLines = maxLines;
        this.sanitizedPackagePrefixes = List.copyOf(sanitizedPackagePrefixes);
    }

    public String format(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(throwable.getClass().getName());
        if (throwable.getMessage() != null) {
            builder.append(": ").append(throwable.getMessage());
        }

        StackTraceElement[] trace = throwable.getStackTrace();
        int limit = maxLines <= 0 ? trace.length : Math.min(maxLines, trace.length);
        for (int i = 0; i < limit; i++) {
            builder.append(System.lineSeparator()).append(formatFrame(trace[i]));
        }
        int remaining = trace.length - limit;
        if (remaining > 0) {
            builder.append(System.lineSeparator()).append("\t... ").append(remaining).append(" more");
        }

        return builder.toString();
    }

    private String formatFrame(StackTraceElement frame) {
        String className = frame.getClassName();
        for (String prefix : sanitizedPackagePrefixes) {
            if (className.startsWith(prefix)) {
                return REDACTED_FRAME;
            }
        }
        return "\tat " + frame;
    }
}
