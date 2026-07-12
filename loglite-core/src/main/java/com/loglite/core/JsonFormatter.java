package com.loglite.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Formats {@link LogEvent} instances as compact JSON using Jackson, omitting null fields.
 */
public class JsonFormatter implements Formatter {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public String format(LogEvent event) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("timestamp", event.timestamp());
        fields.put("level", event.level());
        fields.put("thread", event.threadName());
        fields.put("logger", event.loggerName());
        fields.put("message", event.message());
        fields.put("mdc", event.mdc() == null || event.mdc().isEmpty() ? null : event.mdc());
        fields.put("exception", event.throwable() == null ? null : stackTraceOf(event.throwable()));

        try {
            return MAPPER.writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String stackTraceOf(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString().stripTrailing();
    }
}
