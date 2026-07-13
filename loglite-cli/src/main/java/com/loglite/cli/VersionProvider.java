package com.loglite.cli;

import picocli.CommandLine.IVersionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resolves the CLI's version from {@code version.properties}, which is populated with the
 * Maven project version at build time via resource filtering.
 */
public class VersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/version.properties")) {
            if (in != null) {
                properties.load(in);
            }
        }
        String version = properties.getProperty("version", "unknown");
        return new String[] {"loglite-cli " + version};
    }
}
