package com.loglite.server.controller;

import com.loglite.server.entity.LogEntry;
import com.loglite.server.repository.LogEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice test validating {@link LogQueryController}'s routing and response payload shape,
 * with the repository mocked so no database is required.
 */
@WebMvcTest(LogQueryController.class)
class LogQueryControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogEntryRepository repository;

    @Test
    void queryReturnsPagedEnvelope() throws Exception {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<LogEntry>(List.of()));

        mockMvc.perform(get("/api/v1/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void servicesRoutesToDistinctLoggerNames() throws Exception {
        when(repository.findDistinctLoggerNames()).thenReturn(List.of("service-a", "service-b"));

        mockMvc.perform(get("/api/v1/logs/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("service-a"))
                .andExpect(jsonPath("$[1]").value("service-b"));
    }

    @Test
    void byTraceIdRoutesWithPathVariable() throws Exception {
        UUID id = UUID.randomUUID();
        LogEntry entry = new LogEntry(id, java.time.Instant.now(), "com.example.Foo",
                com.loglite.core.LogLevel.INFO, "traced", "main", java.util.Map.of("traceId", "abc-123"));
        when(repository.findByMetadataKeyValue("traceId", "abc-123")).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/v1/logs/trace/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("traced"));
    }
}
