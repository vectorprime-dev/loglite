package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that concurrent logging from many threads does not corrupt shared
 * appender buffers or logger registry state.
 */
class ThreadSafetyTest {

    @Test
    void concurrentLoggingDoesNotLoseOrCorruptEvents() throws InterruptedException {
        MemoryAppender appender = new MemoryAppender();
        LoggerContext context = LoggerContext.builder()
                .level(LogLevel.INFO)
                .appender(appender)
                .build();

        int threadCount = 16;
        int messagesPerThread = 200;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadIndex = t;
            executor.submit(() -> {
                try {
                    Logger logger = context.getLogger("thread-" + threadIndex);
                    for (int i = 0; i < messagesPerThread; i++) {
                        logger.info("message {}", i);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        assertEquals(threadCount * messagesPerThread, appender.getEvents().size());
    }

    @Test
    void concurrentGetLoggerReturnsSameInstancePerName() throws InterruptedException {
        LoggerContext context = LoggerContext.builder().build();
        int threadCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Logger[] results = new Logger[threadCount];

        for (int t = 0; t < threadCount; t++) {
            final int index = t;
            executor.submit(() -> {
                try {
                    results[index] = context.getLogger("shared-logger");
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        for (Logger logger : results) {
            assertEquals(results[0], logger);
        }
    }
}
