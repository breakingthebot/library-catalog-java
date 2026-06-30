/*
 * src/test/java/cli/CommandLineParserTest.java
 * Verifies CLI argument parsing and validation rules with JUnit 5.
 * Connects to: src/main/java/cli/CommandLineParser.java, src/main/java/models/CommandRequest.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.breakingthebot.librarycatalog.cli.CommandLineParser;
import com.breakingthebot.librarycatalog.cli.CommandName;
import com.breakingthebot.librarycatalog.models.CommandRequest;

/**
 * Tests command-line parsing behavior.
 */
public final class CommandLineParserTest {
    /**
     * Verifies empty input produces help output.
     */
    @Test
    void defaultsToHelpWithDefaultPath() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[0]);

        assertEquals(CommandName.HELP, request.commandName(), "Empty input should map to help.");
        assertTrue(!request.dataPath().toString().isBlank(), "Default data path should be present.");
    }

    /**
     * Verifies command parsing with an overridden data path.
     */
    @Test
    void parsesCustomDataPath() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[] {"add-book", "book-500", "TDD", "Kent Beck", "--data", "tmp/catalog.txt"});

        assertEquals(CommandName.ADD_BOOK, request.commandName(), "Command should parse correctly.");
        assertEquals(Path.of("tmp/catalog.txt"), request.dataPath(), "Data flag should override the path.");
        assertEquals(3, request.arguments().size(), "Book command should keep its three arguments.");
    }

    /**
     * Verifies search command parsing.
     */
    @Test
    void parsesSearchCommand() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[] {"find-book", "pragmatic"});

        assertEquals(CommandName.FIND_BOOK, request.commandName(), "Search command should parse correctly.");
        assertEquals(1, request.arguments().size(), "Search command should keep a single query argument.");
    }

    /**
     * Verifies loan report parsing.
     */
    @Test
    void parsesLoanReportCommand() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[] {"loan-report"});

        assertEquals(CommandName.LOAN_REPORT, request.commandName(), "Loan report command should parse correctly.");
        assertEquals(0, request.arguments().size(), "Loan report should not require arguments.");
    }

    /**
     * Verifies bootstrap parsing.
     */
    @Test
    void parsesBootstrapCommand() {
        CommandLineParser parser = new CommandLineParser();
        CommandRequest request = parser.parse(new String[] {"bootstrap"});

        assertEquals(CommandName.BOOTSTRAP, request.commandName(), "Bootstrap command should parse correctly.");
        assertEquals(0, request.arguments().size(), "Bootstrap should not require arguments.");
    }

    /**
     * Verifies incorrect argument counts are rejected.
     */
    @Test
    void rejectsMissingArguments() {
        CommandLineParser parser = new CommandLineParser();

        assertThrows(
            IllegalArgumentException.class,
            () -> parser.parse(new String[] {"checkout", "book-500"}),
            "Incomplete commands should fail validation."
        );
    }
}

