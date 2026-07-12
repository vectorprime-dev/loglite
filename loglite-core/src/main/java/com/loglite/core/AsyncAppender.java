package com.loglite.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Delegates event writing to a virtual-thread-backed consumer, decoupling the calling
 * thread from the (potentially slow) underlying appender.
 */
public class AsyncAppender implements Appender {

    /** Behavior applied when the internal queue is full. */
    public enum OverflowPolicy {
        /** Block the calling thread until space is available. */
        BLOCK,
        /** Silently discard the event. */
        DROP
    }

    private static final int DEFAULT_CAPACITY = 1024;

    private final Appender delegate;
    private final BlockingQueue<LogEvent> queue;
    private final OverflowPolicy overflowPolicy;
    private final ExecutorService executor;
    private volatile boolean running = true;

    public AsyncAppender(Appender delegate) {
        this(delegate, DEFAULT_CAPACITY, OverflowPolicy.BLOCK);
    }

    public AsyncAppender(Appender delegate, int capacity, OverflowPolicy overflowPolicy) {
        this.delegate = delegate;
        this.queue = new LinkedBlockingQueue<>(capacity);
        this.overflowPolicy = overflowPolicy;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.executor.submit(this::consumeLoop);
    }

    @Override
    public void append(LogEvent event) {
        if (!running) {
            return;
        }
        if (overflowPolicy == OverflowPolicy.DROP) {
            queue.offer(event);
            return;
        }
        try {
            queue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consumeLoop() {
        try {
            while (running || !queue.isEmpty()) {
                LogEvent event = queue.poll(200, TimeUnit.MILLISECONDS);
                if (event != null) {
                    delegate.append(event);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops accepting new events and shuts down the background consumer once
     * the queue has drained.
     */
    public void close() {
        running = false;
        executor.shutdown();
    }
}
