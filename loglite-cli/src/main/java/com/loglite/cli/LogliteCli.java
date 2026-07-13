package com.loglite.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Entry point for the {@code loglite-cli} command-line client.
 */
@Command(name = "loglite-cli")
public class LogliteCli implements Runnable {

    @Override
    public void run() {
        System.out.println("loglite-cli");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new LogliteCli()).execute(args);
        System.exit(exitCode);
    }
}
