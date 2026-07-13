package com.loglite.slf4j;

import com.loglite.core.LogLevel;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;

/**
 * Adapts an {@code org.slf4j.Logger} call onto a {@link com.loglite.core.Logger}, formatting
 * the message and delegating directly since both use {@code {}} placeholder syntax.
 */
class LogliteSlf4jLogger extends LegacyAbstractLogger {

    private static final long serialVersionUID = 1L;

    private final transient com.loglite.core.Logger delegate;

    LogliteSlf4jLogger(String name, com.loglite.core.Logger delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return null;
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {
        String formatted = MessageFormatter.arrayFormat(messagePattern, arguments).getMessage();
        LogLevel coreLevel = toCoreLevel(level);
        Object[] args = throwable != null ? new Object[] {throwable} : new Object[0];

        switch (coreLevel) {
            case TRACE -> delegate.trace(formatted, args);
            case DEBUG -> delegate.debug(formatted, args);
            case INFO -> delegate.info(formatted, args);
            case WARN -> delegate.warn(formatted, args);
            case ERROR -> delegate.error(formatted, args);
            case FATAL -> delegate.fatal(formatted, args);
        }
    }

    private LogLevel toCoreLevel(Level level) {
        return switch (level) {
            case TRACE -> LogLevel.TRACE;
            case DEBUG -> LogLevel.DEBUG;
            case INFO -> LogLevel.INFO;
            case WARN -> LogLevel.WARN;
            case ERROR -> LogLevel.ERROR;
        };
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isEnabled(LogLevel.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isEnabled(LogLevel.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isEnabled(LogLevel.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isEnabled(LogLevel.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isEnabled(LogLevel.ERROR);
    }
}
