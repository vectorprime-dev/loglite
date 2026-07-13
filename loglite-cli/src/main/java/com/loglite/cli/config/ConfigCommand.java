package com.loglite.cli.config;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

/**
 * Saves the target server URL and API key for the active profile to {@code ~/.loglite.json}.
 */
@Command(name = "config", description = "Save connection settings for the active profile.")
public class ConfigCommand implements Callable<Integer> {

    @Option(names = "--url", description = "Loglite server base URL, e.g. http://localhost:8080")
    private String url;

    @Option(names = "--key", description = "API key used to authenticate requests")
    private String apiKey;

    @Override
    public Integer call() throws Exception {
        CliConfig config = ConfigStore.load();
        ConnectionProfile profile = config.getActive();

        if (url != null) {
            profile.setUrl(url);
        }
        if (apiKey != null) {
            profile.setApiKey(apiKey);
        }

        ConfigStore.save(config);
        System.out.println("Saved configuration for profile '" + config.getActiveProfile() + "' to " + ConfigStore.configPath());
        return 0;
    }
}
