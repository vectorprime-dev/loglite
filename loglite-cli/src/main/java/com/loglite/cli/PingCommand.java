package com.loglite.cli;

import com.loglite.cli.client.LogliteApiClient;
import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import picocli.CommandLine.Command;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

/**
 * Verifies that the configured server URL and API key are reachable and valid.
 */
@Command(name = "ping", description = "Check connectivity and credentials against the configured server.")
public class PingCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        CliConfig config = ConfigStore.load();
        LogliteApiClient client = new LogliteApiClient(config.getActive());

        try {
            HttpResponse<String> response = client.get("/api/v1/logs?size=1");
            if (response.statusCode() == 200) {
                System.out.println("OK: connected to " + client.baseUrl() + " (profile '" + config.getActiveProfile() + "')");
                return 0;
            } else if (response.statusCode() == 401) {
                System.err.println("FAILED: server rejected the configured API key (401)");
                return 1;
            } else {
                System.err.println("FAILED: server responded with status " + response.statusCode());
                return 1;
            }
        } catch (Exception e) {
            System.err.println("FAILED: could not reach server - " + e.getMessage());
            return 1;
        }
    }
}
