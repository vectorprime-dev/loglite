package com.loglite.server.ingest;

import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Buffers incoming log entries in memory and flushes them to the database in batches,
 * either once {@link #BATCH_SIZE} entries have accumulated or every second, whichever comes first.
 */
@Component
public class LogBatchWriter {

    private static final int BATCH_SIZE = 500;

    private final LogEntryRepository repository;
    private final Queue<LogEntry> queue = new ConcurrentLinkedQueue<>();

    public LogBatchWriter(LogEntryRepository repository) {
        this.repository = repository;
    }

    /**
     * Enqueues an entry for later batched persistence, flushing immediately if the
     * queue has reached {@link #BATCH_SIZE}.
     */
    public void submit(LogEntry entry) {
        queue.add(entry);
        if (queue.size() >= BATCH_SIZE) {
            flush();
        }
    }

    @Scheduled(fixedRate = 1000)
    public void flush() {
        List<LogEntry> batch = drain();
        if (!batch.isEmpty()) {
            repository.saveAll(batch);
        }
    }

    /**
     * Flushes any remaining buffered entries before the application shuts down.
     */
    @PreDestroy
    public void shutdown() {
        flush();
    }

    private List<LogEntry> drain() {
        List<LogEntry> batch = new ArrayList<>();
        LogEntry entry;
        while ((entry = queue.poll()) != null) {
            batch.add(entry);
        }
        return batch;
    }
}
