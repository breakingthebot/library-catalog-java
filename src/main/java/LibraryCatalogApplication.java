/*
 * src/LibraryCatalogApplication.java
 * Runs the CLI application and maps execution results to process output and exit codes.
 * Connects to: src/main/java/services/LibraryCatalogApplicationRunner.java, src/main/java/models/ApplicationExecutionResult.java
 * Created: 2026-06-30
 */
package src;

import src.models.ApplicationExecutionResult;
import src.services.LibraryCatalogApplicationRunner;

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
     */
    public static void main(String[] args) {
        LibraryCatalogApplicationRunner runner = new LibraryCatalogApplicationRunner();
        ApplicationExecutionResult result = runner.run(args);

        if (!result.output().isBlank()) {
            System.out.println(result.output());
        }

        if (!result.errorOutput().isBlank()) {
            System.err.println(result.errorOutput());
        }

        if (!result.isSuccess()) {
            System.exit(result.exitCode());
        }
    }
}
