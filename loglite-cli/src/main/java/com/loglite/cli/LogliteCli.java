package com.loglite.cli;

import com.loglite.cli.config.ConfigCommand;
import com.loglite.cli.config.ProfilesCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Entry point for the {@code loglite-cli} command-line client. Subcommands are
 * registered here as they are implemented.
 */
@Command(
        name = "loglite-cli",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class,
        description = "Command-line client for querying and uploading Loglite log events.",
        subcommands = {ConfigCommand.class, ProfilesCommand.class, PingCommand.class, QueryCommand.class, ParseCommand.class, WatchCommand.class, StatsCommand.class})
public class LogliteCli implements Runnable {

    @Override
    public void run() {
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new LogliteCli()).execute(args);
        System.exit(exitCode);
    }
}
