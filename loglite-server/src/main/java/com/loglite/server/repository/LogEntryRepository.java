package com.loglite.server.repository;

import com.loglite.server.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository for persisting and retrieving {@link LogEntry} records.
 */
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {

    /**
     * Finds entries whose metadata JSONB column contains the given key/value pair,
     * using Postgres' native {@code ->>} text-extraction operator.
     */
    @Query(value = "SELECT * FROM log_entries WHERE metadata ->> :key = :value", nativeQuery = true)
    List<LogEntry> findByMetadataKeyValue(@Param("key") String key, @Param("value") String value);
}
