package com.loglite.cli;

import com.fasterxml.jackson.core.type.TypeReference;
import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.client.LogliteApiClient;
import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Prints an ASCII bar chart summarizing log counts by level.
 */
@Command(name = "stats", description = "Show aggregate statistics about ingested logs.")
public class StatsCommand implements Callable<Integer> {

    private static final int BAR_WIDTH = 20;

    @Option(names = "--levels", description = "Show counts by severity level as ASCII bars")
    private boolean levels;

    @Override
    public Integer call() throws Exception {
        CliConfig config = ConfigStore.load();
        LogliteApiClient client = new LogliteApiClient(config.getActive());

        HttpResponse<String> response = client.get("/api/v1/logs/stats/levels");
        if (response.statusCode() != 200) {
            System.err.println("Stats request failed: HTTP " + response.statusCode());
            return 1;
        }

        Map<String, Long> counts = JsonSupport.MAPPER.readValue(response.body(), new TypeReference<LinkedHashMap<String, Long>>() {
        });

        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            long count = entry.getValue();
            int filled = total == 0 ? 0 : (int) Math.round((BAR_WIDTH * (double) count) / total);
            String bar = "=".repeat(filled) + " ".repeat(BAR_WIDTH - filled);
            long percent = total == 0 ? 0 : Math.round((100.0 * count) / total);
            System.out.printf("%-6s [%s] %d%%%n", entry.getKey(), bar, percent);
        }
        return 0;
    }
}
