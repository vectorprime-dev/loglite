package com.loglite.cli.model;

import java.util.List;

/**
 * Client-side representation of the paging envelope returned by query endpoints.
 */
public record PagedResponseDto<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}
