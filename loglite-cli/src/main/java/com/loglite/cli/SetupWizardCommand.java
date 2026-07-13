package com.loglite.cli;

import com.loglite.cli.config.CliConfig;
import com.loglite.cli.config.ConfigStore;
import com.loglite.cli.config.ConnectionProfile;
import picocli.CommandLine.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

/**
 * Interactively prompts for and saves connection settings when none are configured yet.
 */
@Command(name = "setup", description = "Interactively configure the connection to a Loglite server.")
public class SetupWizardCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        CliConfig config = ConfigStore.load();
        ConnectionProfile profile = config.getActive();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String url = prompt(reader, "Server URL", profile.getUrl());
            while (url == null || url.isBlank()) {
                System.out.println("A server URL is required.");
                url = prompt(reader, "Server URL", profile.getUrl());
            }
            profile.setUrl(url);

            String apiKey = prompt(reader, "API key", profile.getApiKey());
            if (apiKey != null && !apiKey.isBlank()) {
                profile.setApiKey(apiKey);
            }
        }

        ConfigStore.save(config);
        System.out.println("Saved configuration for profile '" + config.getActiveProfile() + "' to " + ConfigStore.configPath());
        return 0;
    }

    private String prompt(BufferedReader reader, String label, String currentValue) throws IOException {
        String suffix = currentValue != null && !currentValue.isBlank() ? " [" + currentValue + "]" : "";
        System.out.print(label + suffix + ": ");
        System.out.flush();
        String line = reader.readLine();
        if (line == null || line.isBlank()) {
            return currentValue;
        }
        return line.trim();
    }
}
