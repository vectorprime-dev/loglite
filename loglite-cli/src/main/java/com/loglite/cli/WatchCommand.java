package com.loglite.cli;

import com.loglite.cli.client.JsonSupport;
import com.loglite.cli.model.LogEntryDto;
import com.loglite.cli.output.PrettyFormatter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;

/**
 * Watches a local newline-delimited JSON log file for appended lines and prints them as they arrive.
 */
@Command(name = "watch", description = "Watch a local log file for new lines as they are appended.")
public class WatchCommand implements Callable<Integer> {

    @Option(names = "--file", required = true, description = "Path to the log file to watch")
    private Path file;

    @Override
    public Integer call() throws Exception {
        Path absoluteFile = file.toAbsolutePath();
        Path directory = absoluteFile.getParent();
        if (directory == null || !java.nio.file.Files.exists(absoluteFile)) {
            System.err.println("File not found: " + file);
            return 1;
        }

        long position = java.nio.file.Files.size(absoluteFile);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            System.err.println("Watching " + absoluteFile + " for new lines (Ctrl+C to stop)...");

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changed = directory.resolve((Path) event.context());
                    if (changed.equals(absoluteFile)) {
                        position = printNewLines(absoluteFile, position);
                    }
                }
                key.reset();
            }
        }
    }

    private long printNewLines(Path file, long fromPosition) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "r")) {
            long length = raf.length();
            if (length < fromPosition) {
                fromPosition = 0;
            }
            raf.seek(fromPosition);
            String line;
            while ((line = raf.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    LogEntryDto entry = JsonSupport.MAPPER.readValue(line, LogEntryDto.class);
                    System.out.println(PrettyFormatter.format(entry, true));
                } catch (Exception e) {
                    System.out.println(line);
                }
            }
            return raf.getFilePointer();
        }
    }
}
