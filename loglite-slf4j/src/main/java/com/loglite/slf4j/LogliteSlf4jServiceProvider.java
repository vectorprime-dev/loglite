package com.loglite.slf4j;

import org.slf4j.IMarkerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * SLF4J 2.x service provider that routes {@code org.slf4j.Logger} calls to {@code loglite-core}.
 */
public class LogliteSlf4jServiceProvider implements SLF4JServiceProvider {

    public static final String REQUESTED_API_VERSION = "2.0.99";

    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequestedApiVersion() {
        return REQUESTED_API_VERSION;
    }

    @Override
    public void initialize() {
        loggerFactory = new LogliteLoggerFactory();
        markerFactory = new BasicMarkerFactory();
        mdcAdapter = new LogliteMDCAdapter();
    }
}
