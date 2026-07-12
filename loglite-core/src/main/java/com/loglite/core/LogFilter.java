package com.loglite.core;

/**
 * Enables custom interception of log events before they are dispatched to appenders.
 */
public interface LogFilter {

    /**
     * @param event the candidate event
     * @return true if the event should be allowed through, false to discard it
     */
    boolean filter(LogEvent event);
}
