package com.loglite.cli.config;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Manages named connection profiles stored in {@code ~/.loglite.json}.
 */
@Command(
        name = "profile",
        description = "List, add, or switch between connection profiles.",
        subcommands = {
                ProfilesCommand.ListCommand.class,
                ProfilesCommand.AddCommand.class,
                ProfilesCommand.UseCommand.class
        })
public class ProfilesCommand {

    @Command(name = "list", description = "List all configured profiles.")
    static class ListCommand implements Callable<Integer> {
        @Override
        public Integer call() {
            CliConfig config = ConfigStore.load();
            if (config.getProfiles().isEmpty()) {
                System.out.println("No profiles configured.");
                return 0;
            }
            for (Map.Entry<String, ConnectionProfile> entry : config.getProfiles().entrySet()) {
                String marker = entry.getKey().equals(config.getActiveProfile()) ? "* " : "  ";
                System.out.println(marker + entry.getKey() + " -> " + entry.getValue().getUrl());
            }
            return 0;
        }
    }

    @Command(name = "add", description = "Add or update a named profile.")
    static class AddCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Profile name")
        private String name;

        @picocli.CommandLine.Option(names = "--url", description = "Loglite server base URL")
        private String url;

        @picocli.CommandLine.Option(names = "--key", description = "API key used to authenticate requests")
        private String apiKey;

        @Override
        public Integer call() throws Exception {
            CliConfig config = ConfigStore.load();
            ConnectionProfile profile = config.getProfiles().computeIfAbsent(name, n -> new ConnectionProfile());
            if (url != null) {
                profile.setUrl(url);
            }
            if (apiKey != null) {
                profile.setApiKey(apiKey);
            }
            ConfigStore.save(config);
            System.out.println("Saved profile '" + name + "'.");
            return 0;
        }
    }

    @Command(name = "use", description = "Switch the active profile.")
    static class UseCommand implements Callable<Integer> {

        @Parameters(index = "0", description = "Profile name")
        private String name;

        @Override
        public Integer call() throws Exception {
            CliConfig config = ConfigStore.load();
            if (!config.getProfiles().containsKey(name)) {
                System.err.println("Unknown profile '" + name + "'. Add it first with 'profile add'.");
                return 1;
            }
            config.setActiveProfile(name);
            ConfigStore.save(config);
            System.out.println("Active profile is now '" + name + "'.");
            return 0;
        }
    }
}
