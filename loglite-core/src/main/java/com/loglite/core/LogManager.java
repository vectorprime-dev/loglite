package com.loglite.core;

/**
 * Static entry point for obtaining loggers backed by the default {@link LoggerContext}.
 */
public final class LogManager {

    private LogManager() {
    }

    /**
     * @param name the logger name
     * @return the logger registered for this name in the default context
     */
    public static Logger getLogger(String name) {
        return LoggerContext.getInstance().getLogger(name);
    }

    /**
     * @param clazz the class whose fully-qualified name identifies the logger
     * @return the logger registered for this class in the default context
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Registers an appender on the default context, applied to all loggers it manages.
     */
    public static void addAppender(Appender appender) {
        LoggerContext.getInstance().addAppender(appender);
    }
}
