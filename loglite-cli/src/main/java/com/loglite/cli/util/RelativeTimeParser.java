package com.loglite.cli.util;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses CLI date-range arguments that are either an absolute ISO-8601 instant
 * (e.g. {@code 2024-01-01T00:00:00Z}) or a relative duration in the past
 * (e.g. {@code 2h}, {@code 1d}, {@code 30m}, {@code 45s}) resolved against now.
 */
public final class RelativeTimeParser {

    private static final Pattern RELATIVE_PATTERN = Pattern.compile("^(\\d+)([smhd])$");

    private RelativeTimeParser() {
    }

    /**
     * @param value the raw CLI argument
     * @return the resolved instant
     * @throws IllegalArgumentException if the value is neither a valid ISO-8601 instant
     *                                   nor a valid relative duration expression
     */
    public static Instant parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Date value must not be blank");
        }

        Matcher matcher = RELATIVE_PATTERN.matcher(value.trim());
        if (matcher.matches()) {
            long amount = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);
            long seconds = switch (unit) {
                case "s" -> amount;
                case "m" -> amount * 60;
                case "h" -> amount * 3600;
                case "d" -> amount * 86400;
                default -> throw new IllegalArgumentException("Unsupported unit: " + unit);
            };
            return Instant.now().minusSeconds(seconds);
        }

        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date-range expression '" + value + "'. Expected an ISO-8601 instant or a relative "
                            + "duration like '2h', '1d', '30m', '45s'.", e);
        }
    }
}
