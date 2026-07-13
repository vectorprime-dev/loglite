package com.loglite.cli;

import com.fasterxml.jackson.databind.JavaType;
import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.client.LogliteApiClient;
import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import com.loglite.cli.model.LogEntryDto;
import com.loglite.cli.model.PagedResponseDto;
import picocli.CommandLine.Command;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

/**
 * Retrieves log entries from the configured Loglite server and prints them to standard output.
 */
@Command(name = "query", description = "Query logs from the configured server.")
public class QueryCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        CliConfig config = ConfigStore.load();
        LogliteApiClient client = new LogliteApiClient(config.getActive());

        HttpResponse<String> response = client.get("/api/v1/logs");
        if (response.statusCode() != 200) {
            System.err.println("Query failed: HTTP " + response.statusCode() + " - " + response.body());
            return 1;
        }

        JavaType type = JsonSupport.MAPPER.getTypeFactory()
                .constructParametricType(PagedResponseDto.class, LogEntryDto.class);
        PagedResponseDto<LogEntryDto> page = JsonSupport.MAPPER.readValue(response.body(), type);

        for (LogEntryDto entry : page.content()) {
            System.out.println("[" + entry.level() + "] " + entry.timestamp() + " [" + entry.loggerName() + "] " + entry.message());
        }
        return 0;
    }
}
