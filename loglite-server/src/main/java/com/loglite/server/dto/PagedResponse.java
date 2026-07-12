package com.loglite.server.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Paging envelope wrapping a page of query results.
 */
public record PagedResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
