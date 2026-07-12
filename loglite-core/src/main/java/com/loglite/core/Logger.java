package com.loglite.core;

/**
 * Core logging contract exposed to application code.
 */
public interface Logger {

    void trace(String message, Object... args);

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void fatal(String message, Object... args);

    /**
     * @param level the level to check
     * @return true if this logger would emit an event at the given level
     */
    boolean isEnabled(LogLevel level);
}
