/*
 * src/LibraryCatalogApplication.java
 * Parses CLI commands and routes them into the catalog application service.
 * Connects to: src/cli/CommandLineParser.java, src/services/LibraryCatalogCliService.java
 * Created: 2026-06-30
 */
package src;

import java.io.IOException;
import src.cli.CommandLineParser;
import src.models.CommandRequest;
import src.services.LibraryCatalogCliService;

/**
 * Command-line entry point for the interactive catalog CLI.
 */
public final class LibraryCatalogApplication {
    private LibraryCatalogApplication() {
    }

    /**
     * Parses and executes a command-line request.
     *
     * @param args CLI arguments
     * @throws IOException when persistence fails
     */
    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new CommandLineParser();
        LibraryCatalogCliService cliService = new LibraryCatalogCliService();
        CommandRequest request = parser.parse(args);
        String output = cliService.execute(request);
        System.out.println(output);
    }
}
