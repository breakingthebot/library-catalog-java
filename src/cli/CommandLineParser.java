/*
 * src/cli/CommandLineParser.java
 * Parses CLI arguments into validated command requests for the catalog application.
 * Connects to: src/LibraryCatalogApplication.java, src/cli/CommandName.java, src/models/CommandRequest.java, src/config/ApplicationPaths.java
 * Created: 2026-06-30
 */
package src.cli;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import src.config.ApplicationPaths;
import src.models.CommandRequest;

/**
 * Parses raw command-line arguments into structured requests.
 */
public final class CommandLineParser {
    private static final String DATA_FLAG = "--data";

    /**
     * Parses CLI arguments.
     *
     * @param args raw CLI arguments
     * @return parsed command request
     */
    public CommandRequest parse(String[] args) {
        if (args == null || args.length == 0) {
            return new CommandRequest(CommandName.HELP, List.of(), ApplicationPaths.DEFAULT_CATALOG_PATH);
        }

        Path dataPath = ApplicationPaths.DEFAULT_CATALOG_PATH;
        List<String> tokens = new ArrayList<>();

        for (int index = 0; index < args.length; index++) {
            String current = requireValue(args[index], "CLI argument");

            if (DATA_FLAG.equals(current)) {
                if (index + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for --data.");
                }

                dataPath = Path.of(requireValue(args[++index], "data path"));
                continue;
            }

            tokens.add(current.trim());
        }

        if (tokens.isEmpty()) {
            return new CommandRequest(CommandName.HELP, List.of(), dataPath);
        }

        CommandName commandName = parseCommandName(tokens.getFirst());
        List<String> arguments = tokens.subList(1, tokens.size());
        validateArgumentCount(commandName, arguments.size());

        return new CommandRequest(commandName, arguments, dataPath);
    }

    /**
     * Resolves a command token into a supported command name.
     *
     * @param token command token
     * @return supported command name
     */
    private CommandName parseCommandName(String token) {
        return switch (token.toLowerCase()) {
            case "help" -> CommandName.HELP;
            case "seed" -> CommandName.SEED;
            case "add-book" -> CommandName.ADD_BOOK;
            case "add-member" -> CommandName.ADD_MEMBER;
            case "checkout" -> CommandName.CHECKOUT;
            case "return" -> CommandName.RETURN;
            case "list-books" -> CommandName.LIST_BOOKS;
            case "list-members" -> CommandName.LIST_MEMBERS;
            default -> throw new IllegalArgumentException("Unknown command: " + token);
        };
    }

    /**
     * Rejects incorrect argument counts before command execution starts.
     *
     * @param commandName command to validate
     * @param argumentCount provided argument count
     */
    private void validateArgumentCount(CommandName commandName, int argumentCount) {
        int expectedCount = switch (commandName) {
            case HELP, SEED, LIST_BOOKS, LIST_MEMBERS -> 0;
            case ADD_MEMBER, CHECKOUT, RETURN -> 2;
            case ADD_BOOK -> 3;
        };

        if (argumentCount != expectedCount) {
            throw new IllegalArgumentException(
                "Invalid arguments for " + commandName.name().toLowerCase() + ": expected " + expectedCount + "."
            );
        }
    }

    /**
     * Validates required string values.
     *
     * @param value input text
     * @param label field label for error messages
     * @return trimmed text
     */
    private String requireValue(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid " + label + ": value is required.");
        }

        return value.trim();
    }
}
