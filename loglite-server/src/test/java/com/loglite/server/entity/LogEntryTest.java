package com.loglite.server.entity;

import com.loglite.core.LogLevel;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LogEntryTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testPersistsAndReloadsAllFields() {
        UUID id = UUID.randomUUID();
        Instant timestamp = Instant.parse("2026-01-01T00:00:00Z");
        LogEntry entry = new LogEntry(id, timestamp, "com.example.Foo", LogLevel.ERROR, "failure",
                "main", Map.of("userId", "42"));

        entityManager.persist(entry);
        entityManager.flush();
        entityManager.clear();

        LogEntry reloaded = entityManager.find(LogEntry.class, id);

        assertNotNull(reloaded);
        assertEquals(timestamp, reloaded.getTimestamp());
        assertEquals("com.example.Foo", reloaded.getLoggerName());
        assertEquals(LogLevel.ERROR, reloaded.getLevel());
        assertEquals("failure", reloaded.getMessage());
        assertEquals("main", reloaded.getThreadName());
        assertEquals("42", reloaded.getMetadata().get("userId"));
    }
}
