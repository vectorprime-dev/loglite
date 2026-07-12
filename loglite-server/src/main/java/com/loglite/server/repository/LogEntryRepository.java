package com.loglite.server.repository;

import com.loglite.core.LogLevel;
import com.loglite.server.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for persisting and retrieving {@link LogEntry} records. Extends
 * {@link JpaSpecificationExecutor} to support dynamic, criteria-based queries
 * (filtering, sorting, pagination) built from {@link org.springframework.data.jpa.domain.Specification}s.
 */
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID>, JpaSpecificationExecutor<LogEntry> {

    /**
     * Finds entries whose metadata JSONB column contains the given key/value pair,
     * using Postgres' native {@code ->>} text-extraction operator.
     */
    @Query(value = "SELECT * FROM log_entries WHERE metadata ->> :key = :value", nativeQuery = true)
    List<LogEntry> findByMetadataKeyValue(@Param("key") String key, @Param("value") String value);

    /**
     * @return the number of entries recorded at each log level
     */
    @Query("SELECT le.level as level, COUNT(le) as count FROM LogEntry le GROUP BY le.level")
    List<LevelCount> countGroupedByLevel();

    /** Projection for {@link #countGroupedByLevel()}. */
    interface LevelCount {
        LogLevel getLevel();

        long getCount();
    }

    /**
     * Bulk-deletes entries older than the given cutoff, for retention cleanup.
     *
     * @return the number of rows deleted
     */
    @Modifying
    @Query("DELETE FROM LogEntry le WHERE le.timestamp < :cutoff")
    int deleteByTimestampBefore(@Param("cutoff") Instant cutoff);
}
