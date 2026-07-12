package com.loglite.core;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Appender that collects events in memory, intended for use in test assertions.
 */
public class MemoryAppender implements Appender {

    private final List<LogEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public void append(LogEvent event) {
        events.add(event);
    }

    /**
     * @return an unmodifiable view of the events captured so far
     */
    public List<LogEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Removes all captured events.
     */
    public void clear() {
        events.clear();
    }

    /**
     * @param message the message to look for
     * @return true if any captured event's message equals the given message
     */
    public boolean contains(String message) {
        return events.stream().anyMatch(event -> message.equals(event.message()));
    }

    /**
     * @param level   the level to match
     * @param message the message to match
     * @return true if any captured event matches both the level and message
     */
    public boolean contains(LogLevel level, String message) {
        return events.stream().anyMatch(event -> event.level() == level && message.equals(event.message()));
    }
}
