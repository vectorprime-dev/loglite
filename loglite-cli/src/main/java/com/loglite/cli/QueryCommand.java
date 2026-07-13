package com.loglite.cli;

import com.fasterxml.jackson.databind.JavaType;
import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.client.LogliteApiClient;
import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import com.loglite.cli.model.LogEntryDto;
import com.loglite.cli.model.PagedResponseDto;
import com.loglite.cli.output.PrettyFormatter;
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

    @Option(names = "--search", description = "Regular expression matched against each log message")
    private String search;

    @Option(names = "--meta", description = "Metadata filter as key=value, e.g. --meta tenantId=alpha")
    private java.util.Map<String, String> metadata;

    @Option(names = "--limit", defaultValue = "50", description = "Maximum number of results to return")
    private int limit;

    @Option(names = "--offset", defaultValue = "0", description = "Number of results to skip")
    private int offset;

    @Option(names = "--no-color", description = "Disable ANSI color output")
    private boolean noColor;

    @Option(names = "--columns", split = ",", description = "Fields to display: time,level,logger,msg,thread")
    private java.util.List<String> columns;

    @Option(names = "--truncate", description = "Maximum message length before truncating with an ellipsis")
    private Integer truncate;

    @Option(names = "--format", defaultValue = "pretty", description = "Output format: pretty, json, csv, md")
    private String format;

    @Option(names = "--pager", description = "Pipe pretty output through the system pager (less/more)")
    private boolean pager;

    @Option(names = "--tail", description = "Poll the server at intervals and print only newly arrived logs")
    private boolean tail;

    @Option(names = "--tail-interval", defaultValue = "2", description = "Seconds between polls when using --tail")
    private int tailIntervalSeconds;

    @Override
    public Integer call() throws Exception {
        if (limit <= 0) {
            System.err.println("--limit must be a positive integer");
            return 1;
        }
        if (offset < 0) {
            System.err.println("--offset must not be negative");
            return 1;
        }

        CliConfig config = ConfigStore.load();
        LogliteApiClient client = new LogliteApiClient(config.getActive());

        if (tail) {
            return runTail(client);
        }

        java.util.List<LogEntryDto> filtered = fetchAndFilter(client, buildPath());
        return render(filtered);
    }

    private Integer runTail(LogliteApiClient client) throws Exception {
        Instant cursor = from != null ? RelativeTimeParser.parse(from) : Instant.now();
        while (true) {
            java.util.List<LogEntryDto> newEntries = fetchAndFilter(client, buildPath(cursor));
            for (LogEntryDto entry : newEntries) {
                System.out.println(PrettyFormatter.format(entry, !noColor, columns));
                if (entry.timestamp() != null && entry.timestamp().isAfter(cursor)) {
                    cursor = entry.timestamp();
                }
            }
            Thread.sleep(Math.max(1, tailIntervalSeconds) * 1000L);
        }
    }

    private java.util.List<LogEntryDto> fetchAndFilter(LogliteApiClient client, String path) throws Exception {
        HttpResponse<String> response = client.get(path);
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Query failed: HTTP " + response.statusCode() + " - " + response.body());
        }

        JavaType type = JsonSupport.MAPPER.getTypeFactory()
                .constructParametricType(PagedResponseDto.class, LogEntryDto.class);
        PagedResponseDto<LogEntryDto> page = JsonSupport.MAPPER.readValue(response.body(), type);

        java.util.regex.Pattern searchPattern = search != null ? java.util.regex.Pattern.compile(search) : null;
        java.util.List<LogEntryDto> filtered = new java.util.ArrayList<>();
        for (LogEntryDto entry : page.content()) {
            if (searchPattern != null && (entry.message() == null || !searchPattern.matcher(entry.message()).find())) {
                continue;
            }
            filtered.add(entry);
        }
        return filtered;
    }

    private Integer render(java.util.List<LogEntryDto> filtered) throws Exception {
        java.util.regex.Pattern searchPattern = search != null ? java.util.regex.Pattern.compile(search) : null;

        if ("json".equalsIgnoreCase(format)) {
            System.out.println(JsonSupport.MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(filtered));
            return 0;
        }
        if ("csv".equalsIgnoreCase(format)) {
            System.out.print(com.loglite.cli.output.CsvFormatter.format(filtered));
            return 0;
        }
        if ("md".equalsIgnoreCase(format)) {
            System.out.print(com.loglite.cli.output.MarkdownFormatter.format(filtered));
            return 0;
        }

        java.util.List<String> renderedLines = new java.util.ArrayList<>();
        for (LogEntryDto entry : filtered) {
            LogEntryDto toPrint = entry;
            if (truncate != null && toPrint.message() != null) {
                toPrint = new LogEntryDto(toPrint.id(), toPrint.timestamp(), toPrint.loggerName(), toPrint.level(),
                        com.loglite.cli.output.Truncator.truncate(toPrint.message(), truncate), toPrint.threadName(), toPrint.metadata());
            }
            if (searchPattern != null && !noColor && toPrint.message() != null) {
                String highlighted = com.loglite.cli.output.Highlighter.highlight(toPrint.message(), searchPattern);
                toPrint = new LogEntryDto(toPrint.id(), toPrint.timestamp(), toPrint.loggerName(), toPrint.level(),
                        highlighted, toPrint.threadName(), toPrint.metadata());
            }
            renderedLines.add(PrettyFormatter.format(toPrint, !noColor, columns));
        }

        if (pager) {
            com.loglite.cli.output.Pager.page(renderedLines);
        } else {
            renderedLines.forEach(System.out::println);
        }
        return 0;
    }

    private String buildPath() {
        return buildPath(from != null ? RelativeTimeParser.parse(from) : null);
    }

    private String buildPath(Instant fromOverride) {
        StringBuilder path = new StringBuilder("/api/v1/logs");
        StringBuilder query = new StringBuilder();

        if (fromOverride != null) {
            appendParam(query, "from", fromOverride);
        }
        if (to != null) {
            appendParam(query, "to", RelativeTimeParser.parse(to));
        }
        if (levels != null) {
            for (String level : levels) {
                appendParam(query, "level", level.trim().toUpperCase());
            }
        }
        if (metadata != null) {
            for (var entry : metadata.entrySet()) {
                appendParam(query, "metadata." + entry.getKey(), entry.getValue());
            }
        }
        appendParam(query, "size", String.valueOf(limit));
        appendParam(query, "page", String.valueOf(offset / limit));

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
