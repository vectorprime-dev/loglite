package com.loglite.core;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AsyncAppender}.
 */
public class AsyncAppenderTest {

    @Test
    public void testEventsAreDeliveredToDelegate() throws InterruptedException {
        List<LogEvent> received = new CopyOnWriteArrayList<>();
        AsyncAppender appender = new AsyncAppender(received::add);

        appender.append(eventWithMessage("one"));
        appender.append(eventWithMessage("two"));

        waitUntil(() -> received.size() == 2, 2000);
        assertEquals("one", received.get(0).message());
        assertEquals("two", received.get(1).message());

        appender.close();
    }

    @Test
    public void testDropPolicyDiscardsEventsWhenQueueIsFull() throws InterruptedException {
        CountDownLatch consumerGate = new CountDownLatch(1);
        List<LogEvent> received = Collections.synchronizedList(new java.util.ArrayList<>());
        Appender blockingDelegate = event -> {
            try {
                consumerGate.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            received.add(event);
        };

        AsyncAppender appender = new AsyncAppender(blockingDelegate, 1, AsyncAppender.OverflowPolicy.DROP);

        appender.append(eventWithMessage("taken-by-consumer"));
        Thread.sleep(100); // allow the consumer to pick up the first event and block on the gate

        appender.append(eventWithMessage("fills-queue"));
        appender.append(eventWithMessage("dropped"));

        consumerGate.countDown();
        waitUntil(() -> received.size() == 2, 2000);

        assertEquals("taken-by-consumer", received.get(0).message());
        assertEquals("fills-queue", received.get(1).message());

        appender.close();
    }

    private LogEvent eventWithMessage(String message) {
        return LogEvent.builder().timestamp(Instant.now()).level(LogLevel.INFO).message(message).build();
    }

    private void waitUntil(java.util.function.BooleanSupplier condition, long timeoutMillis) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                fail("condition not met within timeout");
            }
            Thread.sleep(20);
        }
    }
}
