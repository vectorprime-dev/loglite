package com.loglite.slf4j;

import com.loglite.core.LogManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates and caches {@link LogliteSlf4jLogger} adapters backed by {@link LogManager}.
 */
public class LogliteLoggerFactory implements ILoggerFactory {

    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    @Override
    public Logger getLogger(String name) {
        return loggers.computeIfAbsent(name, n -> new LogliteSlf4jLogger(n, LogManager.getLogger(n)));
    }
}
