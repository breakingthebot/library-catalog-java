/*
 * src/test/java/services/LibraryCatalogApplicationRunnerTest.java
 * Verifies CLI boundary error handling and success behavior for the application runner.
 * Connects to: src/main/java/services/LibraryCatalogApplicationRunner.java, src/main/java/models/ApplicationExecutionResult.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.breakingthebot.librarycatalog.services.LibraryCatalogApplicationRunner;

/**
 * Tests top-level application execution behavior.
 */
public final class LibraryCatalogApplicationRunnerTest {
    /**
     * Verifies successful commands return output with a zero exit code.
     *
     * @throws IOException when file setup fails
     */
    @Test
    void returnsSuccessForValidCommand() throws IOException {
        Path tempFile = Files.createTempFile("library-app-runner-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogApplicationRunner runner = new LibraryCatalogApplicationRunner();

        var result = runner.run(new String[] {"seed", "--data", tempFile.toString()});

        assertEquals(0, result.exitCode(), "Successful commands should return exit code zero.");
        assertTrue(result.output().contains("Seeded catalog"), "Successful commands should produce standard output.");
        assertEquals("", result.errorOutput(), "Successful commands should not produce error output.");
    }

    /**
     * Verifies bootstrap succeeds on a missing file with a zero exit code.
     *
     * @throws IOException when file setup fails
     */
    @Test
    void returnsSuccessForBootstrapOnMissingFile() throws IOException {
        Path tempFile = Files.createTempFile("library-app-bootstrap-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogApplicationRunner runner = new LibraryCatalogApplicationRunner();

        var result = runner.run(new String[] {"bootstrap", "--data", tempFile.toString()});

        assertEquals(0, result.exitCode(), "Bootstrap should succeed on a missing file.");
        assertTrue(result.output().contains("Bootstrapped catalog"), "Bootstrap should report created sample data.");
    }

    /**
     * Verifies invalid commands return a user-facing failure result.
     */
    @Test
    void returnsUserErrorForUnknownCommand() {
        LibraryCatalogApplicationRunner runner = new LibraryCatalogApplicationRunner();

        var result = runner.run(new String[] {"unknown-command"});

        assertEquals(1, result.exitCode(), "Unknown commands should return the user error exit code.");
        assertTrue(result.errorOutput().contains("Unknown command"), "Unknown commands should explain the failure.");
        assertTrue(result.errorOutput().contains("Run 'help'"), "Unknown commands should include a help hint.");
    }

    /**
     * Verifies domain validation errors are translated into user-facing failures.
     *
     * @throws IOException when file setup fails
     */
    @Test
    void returnsUserErrorForBusinessRuleFailures() throws IOException {
        Path tempFile = Files.createTempFile("library-app-runner-rules-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogApplicationRunner runner = new LibraryCatalogApplicationRunner();
        runner.run(new String[] {"seed", "--data", tempFile.toString()});

        var result = runner.run(new String[] {"seed", "--data", tempFile.toString()});

        assertEquals(1, result.exitCode(), "Business rule failures should return the user error exit code.");
        assertTrue(result.errorOutput().contains("Seed command requires an empty catalog file"), "Business rule failures should preserve the reason.");
    }
}

