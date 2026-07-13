package com.loglite.cli.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Shared, pre-configured Jackson mapper for (de)serializing server payloads.
 */
public final class JsonSupport {

    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private JsonSupport() {
    }
}
