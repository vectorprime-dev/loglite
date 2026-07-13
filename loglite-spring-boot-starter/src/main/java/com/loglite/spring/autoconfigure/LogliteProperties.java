package com.loglite.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for forwarding this application's logs to a loglite-server instance.
 */
@ConfigurationProperties(prefix = "loglite")
public class LogliteProperties {

    /** Whether log forwarding is enabled. */
    private boolean enabled = true;

    /** Base URL of the target loglite-server instance. */
    private String url;

    /** API key used to authenticate with loglite-server. */
    private String apiKey;

    /** Maximum number of events buffered before a forced flush. */
    private int batchSize = 100;

    /** Milliseconds between scheduled flushes. */
    private long flushIntervalMillis = 1000;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public long getFlushIntervalMillis() {
        return flushIntervalMillis;
    }

    public void setFlushIntervalMillis(long flushIntervalMillis) {
        this.flushIntervalMillis = flushIntervalMillis;
    }
}
