package com.loglite.cli;

import picocli.AutoComplete;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * Generates a bash/zsh-compatible completion script for {@code loglite-cli}.
 * Usage: {@code loglite-cli completion > loglite-cli_completion.sh}
 */
@Command(name = "completion", description = "Generate a bash/zsh completion script.")
public class CompletionCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        CommandLine root = new CommandLine(new LogliteCli());
        String script = AutoComplete.bash(root.getCommandName(), root);
        System.out.println(script);
        return 0;
    }
}
