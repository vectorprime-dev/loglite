package com.loglite.cli;

import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.model.LogEntryDto;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Reads a local newline-delimited JSON log file and prints the parsed entries.
 */
@Command(name = "parse", description = "Parse a local newline-delimited JSON log file.")
public class ParseCommand implements Callable<Integer> {

    @Option(names = "--file", required = true, description = "Path to a newline-delimited JSON log file")
    private Path file;

    @Override
    public Integer call() throws Exception {
        if (!Files.exists(file)) {
            System.err.println("File not found: " + file);
            return 1;
        }

        List<String> lines = Files.readAllLines(file);
        int parsed = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            LogEntryDto entry = JsonSupport.MAPPER.readValue(line, LogEntryDto.class);
            System.out.println("[" + entry.level() + "] " + entry.timestamp() + " [" + entry.loggerName() + "] " + entry.message());
            parsed++;
        }
        System.err.println(parsed + " entries parsed from " + file);
        return 0;
    }
}
