package com.loglite.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MDC}.
 */
public class MDCTest {

    @AfterEach
    public void tearDown() {
        MDC.clear();
    }

    @Test
    public void testPutAndGet() {
        MDC.put("userId", "123");
        assertEquals("123", MDC.get("userId"));
    }

    @Test
    public void testRemove() {
        MDC.put("userId", "123");
        MDC.remove("userId");
        assertNull(MDC.get("userId"));
    }

    @Test
    public void testClear() {
        MDC.put("a", "1");
        MDC.put("b", "2");
        MDC.clear();
        assertTrue(MDC.getCopyOfContextMap().isEmpty());
    }

    @Test
    public void testContextIsThreadIsolated() throws InterruptedException {
        MDC.put("key", "main-thread-value");

        AtomicReference<String> otherThreadValue = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Thread other = new Thread(() -> {
            otherThreadValue.set(MDC.get("key"));
            latch.countDown();
        });
        other.start();
        latch.await();

        assertNull(otherThreadValue.get());
        assertEquals("main-thread-value", MDC.get("key"));
    }
}
