/*
 * src/main/java/services/LibraryCatalogApplicationRunner.java
 * Translates CLI parse and execution failures into stable user-facing results.
 * Connects to: src/main/java/cli/CommandLineParser.java, src/main/java/services/LibraryCatalogCliService.java, src/main/java/models/ApplicationExecutionResult.java
 * Created: 2026-06-30
 */
package src.services;

import java.io.IOException;
import src.cli.CommandLineParser;
import src.models.ApplicationExecutionResult;
import src.models.CommandRequest;

/**
 * Safe application runner that isolates CLI boundary error handling.
 */
public final class LibraryCatalogApplicationRunner {
    private static final int USER_ERROR_EXIT_CODE = 1;
    private static final int IO_ERROR_EXIT_CODE = 2;

    private final CommandLineParser parser;
    private final LibraryCatalogCliService cliService;

    /**
     * Creates the runner with standard dependencies.
     */
    public LibraryCatalogApplicationRunner() {
        this(new CommandLineParser(), new LibraryCatalogCliService());
    }

    /**
     * Creates the runner with injected dependencies.
     *
     * @param parser CLI parser
     * @param cliService CLI application service
     */
    public LibraryCatalogApplicationRunner(CommandLineParser parser, LibraryCatalogCliService cliService) {
        if (parser == null) {
            throw new IllegalArgumentException("Parser is required.");
        }

        if (cliService == null) {
            throw new IllegalArgumentException("CLI service is required.");
        }

        this.parser = parser;
        this.cliService = cliService;
    }

    /**
     * Runs the application safely and returns a stable execution result.
     *
     * @param args raw CLI arguments
     * @return execution result
     */
    public ApplicationExecutionResult run(String[] args) {
        try {
            CommandRequest request = parser.parse(args);
            String output = cliService.execute(request);
            return ApplicationExecutionResult.success(output);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return ApplicationExecutionResult.failure(
                "Error: " + exception.getMessage() + System.lineSeparator() + "Run 'help' to see supported commands.",
                USER_ERROR_EXIT_CODE
            );
        } catch (IOException exception) {
            return ApplicationExecutionResult.failure(
                "Error: Unable to access the catalog file. " + exception.getMessage(),
                IO_ERROR_EXIT_CODE
            );
        }
    }
}
