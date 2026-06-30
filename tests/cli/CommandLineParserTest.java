/*
 * tests/cli/CommandLineParserTest.java
 * Verifies CLI argument parsing and validation rules.
 * Connects to: src/cli/CommandLineParser.java, src/models/CommandRequest.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.cli;

import java.nio.file.Path;
import src.cli.CommandLineParser;
import src.cli.CommandName;
import src.models.CommandRequest;
import tests.utils.TestAssertions;

/**
 * Tests command-line parsing behavior.
 */
public final class CommandLineParserTest {
    private CommandLineParserTest() {
    }

    /**
     * Runs all parser tests.
     */
    public static void runAll() {
        defaultsToHelpWithDefaultPath();
        parsesCustomDataPath();
        rejectsMissingArguments();
    }

    /**
     * Verifies empty input produces help output.
     */
    private static void defaultsToHelpWithDefaultPath() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[0]);

        TestAssertions.assertEquals(CommandName.HELP, request.commandName(), "Empty input should map to help.");
        TestAssertions.assertTrue(!request.dataPath().toString().isBlank(), "Default data path should be present.");
    }

    /**
     * Verifies command parsing with an overridden data path.
     */
    private static void parsesCustomDataPath() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[] {"add-book", "book-500", "TDD", "Kent Beck", "--data", "tmp/catalog.txt"});

        TestAssertions.assertEquals(CommandName.ADD_BOOK, request.commandName(), "Command should parse correctly.");
        TestAssertions.assertEquals(Path.of("tmp/catalog.txt"), request.dataPath(), "Data flag should override the path.");
        TestAssertions.assertEquals(3, request.arguments().size(), "Book command should keep its three arguments.");
    }

    /**
     * Verifies incorrect argument counts are rejected.
     */
    private static void rejectsMissingArguments() {
        CommandLineParser parser = new CommandLineParser();

        TestAssertions.assertThrows(
            () -> parser.parse(new String[] {"checkout", "book-500"}),
            IllegalArgumentException.class,
            "Incomplete commands should fail validation."
        );
    }
}
