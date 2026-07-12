package com.loglite.core;

/**
 * Converts a {@link LogEvent} into its string representation for output.
 */
public interface Formatter {

    /**
     * @param event the event to format
     * @return the formatted string representation of the event
     */
    String format(LogEvent event);
}
