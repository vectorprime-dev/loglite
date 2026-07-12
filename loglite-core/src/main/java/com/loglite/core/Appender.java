package com.loglite.core;

/**
 * Receives {@link LogEvent} instances emitted by a {@link Logger} and writes them somewhere.
 */
public interface Appender {

    /**
     * Handles a single log event, e.g. by writing it to console, file, or a remote endpoint.
     *
     * @param event the log event to append
     */
    void append(LogEvent event);
}
