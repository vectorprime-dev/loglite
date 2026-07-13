package com.loglite.cli;

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
        description = "Command-line client for querying and uploading Loglite log events.")
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
