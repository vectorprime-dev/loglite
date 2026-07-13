package com.loglite.cli.client;

import com.loglite.cli.config.ConnectionProfile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Thin HTTP client wrapper for talking to a {@code loglite-server} instance using
 * the active connection profile's URL and API key.
 */
public class LogliteApiClient {

    private static final String API_KEY_HEADER = "X-API-Key";

    private final ConnectionProfile profile;
    private final HttpClient httpClient;

    public LogliteApiClient(ConnectionProfile profile) {
        this.profile = profile;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String baseUrl() {
        if (profile.getUrl() == null || profile.getUrl().isBlank()) {
            throw new IllegalStateException("No server URL configured. Run 'loglite-cli config --url <url>' first.");
        }
        String url = profile.getUrl();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public HttpRequest.Builder requestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(baseUrl() + path))
                .timeout(Duration.ofSeconds(30));
        if (profile.getApiKey() != null && !profile.getApiKey().isBlank()) {
            builder.header(API_KEY_HEADER, profile.getApiKey());
        }
        return builder;
    }

    public HttpResponse<String> get(String path) throws IOException, InterruptedException {
        HttpRequest request = requestBuilder(path).GET().build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String path, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = requestBuilder(path)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpClient rawClient() {
        return httpClient;
    }
}
