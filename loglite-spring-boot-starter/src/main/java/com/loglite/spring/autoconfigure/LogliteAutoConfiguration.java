package com.loglite.spring.autoconfigure;

import ch.qos.logback.classic.LoggerContext;
import com.loglite.logback.LogliteHttpAppender;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Attaches a {@link LogliteHttpAppender} to the Logback root logger when a log forwarding
 * URL is configured, so application logs are automatically shipped to loglite-server.
 */
@AutoConfiguration
@EnableConfigurationProperties(LogliteProperties.class)
@ConditionalOnProperty(prefix = "loglite", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogliteAutoConfiguration {

    private final LogliteProperties properties;

    public LogliteAutoConfiguration(LogliteProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void attachAppender() {
        if (properties.getUrl() == null || properties.getUrl().isBlank()) {
            return;
        }
        if (!(LoggerFactory.getILoggerFactory() instanceof LoggerContext loggerContext)) {
            return;
        }

        LogliteHttpAppender appender = new LogliteHttpAppender();
        appender.setContext(loggerContext);
        appender.setUrl(properties.getUrl());
        appender.setApiKey(properties.getApiKey());
        appender.setBatchSize(properties.getBatchSize());
        appender.setFlushIntervalMillis(properties.getFlushIntervalMillis());
        appender.start();

        loggerContext.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(appender);
    }
}
