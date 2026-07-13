package com.loglite.server.integration;

import com.loglite.core.LogLevel;
import com.loglite.server.dto.LogIngestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises log ingestion and metadata-filtered querying against a real Postgres
 * instance, run via Testcontainers. Unlike the H2-backed unit tests, this proves
 * migrations apply cleanly and that Postgres-specific query features (JSONB
 * metadata lookups) actually work.
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PostgresIngestionIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("loglite")
            .withUsername("loglite")
            .withPassword("loglite");

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    void ingestedEntryIsFindableByMetadata() throws Exception {
        LogIngestRequest request = new LogIngestRequest(
                Instant.now(), "com.example.Foo", LogLevel.ERROR, "payment failed", "main",
                Map.of("tenantId", "acme-corp"));

        // Use the bulk endpoint, which persists synchronously, so the entry is
        // immediately visible to the query below (the single-entry endpoint buffers
        // writes and flushes on a 1s schedule).
        mockMvc.perform(post("/api/v1/logs/bulk")
                        .header("X-API-Key", "changeme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.List.of(request))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/logs")
                        .header("X-API-Key", "changeme")
                        .param("metadata.tenantId", "acme-corp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].message").value("payment failed"));
    }
}
