package com.loglite.server.repository;

import com.loglite.server.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for persisting and retrieving {@link LogEntry} records.
 */
public interface LogEntryRepository extends JpaRepository<LogEntry, UUID> {
}
