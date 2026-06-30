/*
 * src/models/CommandRequest.java
 * Holds parsed CLI command details including target data path and arguments.
 * Connects to: src/cli/CommandLineParser.java, src/services/LibraryCatalogCliService.java
 * Created: 2026-06-30
 */
package src.models;

import java.nio.file.Path;
import java.util.List;
import src.cli.CommandName;

/**
 * Immutable representation of a parsed command-line request.
 *
 * @param commandName parsed command
 * @param arguments command arguments excluding flags
 * @param dataPath resolved catalog data path
 */
public record CommandRequest(CommandName commandName, List<String> arguments, Path dataPath) {
    /**
     * Creates an immutable request with defensive copies.
     *
     * @param commandName parsed command
     * @param arguments command arguments excluding flags
     * @param dataPath resolved catalog data path
     */
    public CommandRequest {
        if (commandName == null) {
            throw new IllegalArgumentException("Command name is required.");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("Command arguments are required.");
        }

        if (dataPath == null) {
            throw new IllegalArgumentException("Data path is required.");
        }

        arguments = List.copyOf(arguments);
    }
}
