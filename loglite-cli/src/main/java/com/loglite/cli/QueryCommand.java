package com.loglite.cli;

import com.fasterxml.jackson.databind.JavaType;
import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.client.LogliteApiClient;
import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import com.loglite.cli.model.LogEntryDto;
import com.loglite.cli.model.PagedResponseDto;
import com.loglite.cli.util.RelativeTimeParser;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * Retrieves log entries from the configured Loglite server and prints them to standard output.
 */
@Command(name = "query", description = "Query logs from the configured server.")
public class QueryCommand implements Callable<Integer> {

    @Option(names = "--from", description = "Start of the time range: ISO-8601 instant or relative duration (e.g. 2h, 1d)")
    private String from;

    @Option(names = "--to", description = "End of the time range: ISO-8601 instant or relative duration (e.g. 30m)")
    private String to;

    @Option(names = "--level", split = ",", description = "Restrict results to these levels, e.g. ERROR,WARN")
    private String[] levels;

    @Override
    public Integer call() throws Exception {
        CliConfig config = ConfigStore.load();
        LogliteApiClient client = new LogliteApiClient(config.getActive());

        HttpResponse<String> response = client.get(buildPath());
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

    private String buildPath() {
        StringBuilder path = new StringBuilder("/api/v1/logs");
        StringBuilder query = new StringBuilder();

        if (from != null) {
            appendParam(query, "from", RelativeTimeParser.parse(from));
        }
        if (to != null) {
            appendParam(query, "to", RelativeTimeParser.parse(to));
        }
        if (levels != null) {
            for (String level : levels) {
                appendParam(query, "level", level.trim().toUpperCase());
            }
        }

        if (!query.isEmpty()) {
            path.append('?').append(query);
        }
        return path.toString();
    }

    private void appendParam(StringBuilder query, String name, Instant value) {
        appendParam(query, name, value.toString());
    }

    private void appendParam(StringBuilder query, String name, String value) {
        if (!query.isEmpty()) {
            query.append('&');
        }
        query.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8));
    }
}
