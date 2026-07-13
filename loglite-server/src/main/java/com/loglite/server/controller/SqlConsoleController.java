package com.loglite.server.controller;

import com.loglite.server.dto.SqlQueryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Runs ad-hoc, read-only SQL against the log store. Only single {@code SELECT}
 * statements are permitted; any statement that could mutate data or chain multiple
 * statements is rejected before it reaches the database.
 */
@RestController
@RequestMapping("/api/v1/logs")
public class SqlConsoleController {

    private static final int MAX_ROWS = 1000;
    private static final Pattern FORBIDDEN_KEYWORDS = Pattern.compile(
            "\\b(insert|update|delete|drop|alter|truncate|create|grant|revoke|exec|execute|call|merge|copy)\\b",
            Pattern.CASE_INSENSITIVE);

    private final JdbcTemplate jdbcTemplate;

    public SqlConsoleController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/query-sql")
    public List<Map<String, Object>> runQuery(@RequestBody SqlQueryRequest request) {
        String sql = validate(request.sql());
        try {
            return jdbcTemplate.queryForList("SELECT * FROM (" + sql + ") AS console_query LIMIT " + MAX_ROWS);
        } catch (org.springframework.dao.DataAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid query: " + e.getMostSpecificCause().getMessage());
        }
    }

    private String validate(String rawSql) {
        if (rawSql == null || rawSql.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sql must not be blank");
        }
        String sql = rawSql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }
        if (sql.contains(";")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only a single statement is allowed");
        }
        if (!sql.toLowerCase().startsWith("select")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SELECT statements are allowed");
        }
        if (FORBIDDEN_KEYWORDS.matcher(sql).find()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statement contains a disallowed keyword");
        }
        return sql;
    }
}
