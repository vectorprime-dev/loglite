package com.loglite.cli.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads and writes the CLI's persisted configuration file at {@code ~/.loglite.json}.
 */
public final class ConfigStore {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private ConfigStore() {
    }

    public static Path configPath() {
        return Path.of(System.getProperty("user.home"), ".loglite.json");
    }

    public static CliConfig load() {
        Path path = configPath();
        if (!Files.exists(path)) {
            return new CliConfig();
        }
        try {
            return MAPPER.readValue(path.toFile(), CliConfig.class);
        } catch (IOException e) {
            return new CliConfig();
        }
    }

    public static void save(CliConfig config) throws IOException {
        Path path = configPath();
        MAPPER.writeValue(path.toFile(), config);
    }
}
