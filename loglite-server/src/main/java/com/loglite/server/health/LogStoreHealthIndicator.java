package com.loglite.server.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Reports whether the backing log store database is reachable.
 */
@Component
public class LogStoreHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public LogStoreHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return Health.up().withDetail("database", "reachable").build();
            }
            return Health.down().withDetail("database", "not valid").build();
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
