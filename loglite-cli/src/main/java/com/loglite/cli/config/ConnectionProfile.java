package com.loglite.cli.config;

/**
 * Connection settings (server URL and API key) for a single named CLI profile.
 */
public class ConnectionProfile {

    private String url;
    private String apiKey;

    public ConnectionProfile() {
    }

    public ConnectionProfile(String url, String apiKey) {
        this.url = url;
        this.apiKey = apiKey;
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
}
