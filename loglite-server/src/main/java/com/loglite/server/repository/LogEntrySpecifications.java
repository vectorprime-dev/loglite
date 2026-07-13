package com.loglite.server.repository;

import com.loglite.core.LogLevel;
import com.loglite.server.entity.LogEntry;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Reusable, composable {@link Specification} factory methods for building dynamic
 * {@link LogEntry} queries from level, timestamp, logger name, and metadata criteria.
 */
public final class LogEntrySpecifications {

    private LogEntrySpecifications() {
    }

    public static Specification<LogEntry> timestampBetween(Instant from, Instant to) {
        return (root, query, cb) -> cb.between(root.get("timestamp"), from, to);
    }

    public static Specification<LogEntry> levelIn(Collection<LogLevel> levels) {
        return (root, query, cb) -> root.get("level").in(levels);
    }

    /**
     * Matches entries whose logger name exactly equals, or starts with, any of the
     * comma-separated candidate names.
     */
    public static Specification<LogEntry> loggerNameMatches(String commaSeparatedNames) {
        List<String> names = Arrays.stream(commaSeparatedNames.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .toList();

        return (root, query, cb) -> {
            List<Predicate> predicates = names.stream()
                    .map(name -> cb.or(
                            cb.equal(root.get("loggerName"), name),
                            cb.like(root.get("loggerName"), name + "%")))
                    .toList();
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Matches entries whose metadata JSONB column has the given key mapped to the given value,
     * via Postgres' {@code jsonb_extract_path_text} function.
     */
    public static Specification<LogEntry> metadataEquals(String key, String value) {
        return (root, query, cb) -> cb.equal(
                cb.function("jsonb_extract_path_text", String.class, root.get("metadata"), cb.literal(key)),
                value);
    }

    public static Specification<LogEntry> messageContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("message")), "%" + search.toLowerCase() + "%");
    }

    /**
     * Matches entries whose message contains an embedded exception/stack trace, identified by
     * either an {@code "Exception"}/{@code "Error"} type name or a {@code "\tat "} stack frame line.
     */
    public static Specification<LogEntry> hasException() {
        return (root, query, cb) -> cb.or(
                cb.like(root.get("message"), "%Exception%"),
                cb.like(root.get("message"), "%Error%"),
                cb.like(root.get("message"), "%\n\tat %"));
    }
}
