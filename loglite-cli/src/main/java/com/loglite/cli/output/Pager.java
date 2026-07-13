package com.loglite.cli.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Pipes output through the system pager ({@code less} on Linux/macOS, {@code more} on Windows),
 * falling back to printing in fixed-size chunks if no pager command is available.
 */
public final class Pager {

    private static final int FALLBACK_CHUNK_SIZE = 20;

    private Pager() {
    }

    public static void page(List<String> lines) {
        String pagerCommand = System.getProperty("os.name", "").toLowerCase().contains("win") ? "more" : "less";

        try {
            Process process = new ProcessBuilder(pagerCommand)
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start();
            try (OutputStream out = process.getOutputStream()) {
                for (String line : lines) {
                    out.write((line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
                }
            }
            process.waitFor();
            return;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            // Pager command unavailable - fall back to chunked printing below.
        }

        pageInChunks(lines);
    }

    private static void pageInChunks(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            System.out.println(lines.get(i));
            if ((i + 1) % FALLBACK_CHUNK_SIZE == 0 && i + 1 < lines.size()) {
                System.out.println("-- more (" + (i + 1) + "/" + lines.size() + ") --");
            }
        }
    }
}
