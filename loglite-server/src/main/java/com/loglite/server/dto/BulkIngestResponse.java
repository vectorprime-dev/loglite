package com.loglite.server.dto;

/**
 * Response returned by the bulk log ingestion endpoint.
 */
public record BulkIngestResponse(int count, String status) {
}
