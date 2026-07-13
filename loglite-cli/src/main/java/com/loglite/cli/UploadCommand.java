package com.loglite.cli;

import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.client.LogliteApiClient;
import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import com.loglite.cli.model.IngestPayload;
import com.loglite.cli.model.LogEntryDto;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Reads a local newline-delimited JSON log file and uploads its entries to the server
 * in fixed-size batches.
 */
@Command(name = "upload", description = "Upload a local JSON log file to the server in batches.")
public class UploadCommand implements Callable<Integer> {

    private static final int BATCH_SIZE = 100;

    @Option(names = "--file", required = true, description = "Path to a newline-delimited JSON log file")
    private Path file;

    @Override
    public Integer call() throws Exception {
        if (!Files.exists(file)) {
            System.err.println("File not found: " + file);
            return 1;
        }

        CliConfig config = ConfigStore.load();
        LogliteApiClient client = new LogliteApiClient(config.getActive());

        List<IngestPayload> batch = new ArrayList<>(BATCH_SIZE);
        int uploaded = 0;

        for (String line : Files.readAllLines(file)) {
            if (line.isBlank()) {
                continue;
            }
            LogEntryDto entry = JsonSupport.MAPPER.readValue(line, LogEntryDto.class);
            batch.add(IngestPayload.from(entry));

            if (batch.size() == BATCH_SIZE) {
                uploaded += sendBatch(client, batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            uploaded += sendBatch(client, batch);
        }

        System.out.println("Uploaded " + uploaded + " entries from " + file);
        return 0;
    }

    private int sendBatch(LogliteApiClient client, List<IngestPayload> batch) throws Exception {
        String json = JsonSupport.MAPPER.writeValueAsString(batch);
        HttpResponse<String> response = client.post("/api/v1/logs/bulk", json);
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Upload failed: HTTP " + response.statusCode() + " - " + response.body());
        }
        return batch.size();
    }
}
