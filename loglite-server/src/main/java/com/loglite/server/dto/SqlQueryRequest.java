package com.loglite.server.dto;

/**
 * Request body for the read-only SQL console endpoint.
 */
public record SqlQueryRequest(String sql) {
}
