/*
 * tests/services/LibraryCatalogCliServiceTest.java
 * Verifies the interactive CLI service executes commands against persisted catalog data.
 * Connects to: src/services/LibraryCatalogCliService.java, src/cli/*.java, src/models/*.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import src.cli.CatalogConsoleFormatter;
import src.cli.CommandName;
import src.models.CommandRequest;
import src.services.CatalogPersistenceService;
import src.services.LibraryCatalogCliService;
import tests.utils.TestAssertions;

/**
 * Tests the application-level CLI workflow.
 */
public final class LibraryCatalogCliServiceTest {
    private LibraryCatalogCliServiceTest() {
    }

    /**
     * Runs all CLI service tests.
     *
     * @throws IOException when file IO fails
     */
    public static void runAll() throws IOException {
        seedsEmptyCatalog();
        mutatesAndListsCatalog();
    }

    /**
     * Verifies the seed command initializes a new catalog file.
     *
     * @throws IOException when file IO fails
     */
    private static void seedsEmptyCatalog() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-seed-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        String output = service.execute(new CommandRequest(CommandName.SEED, java.util.List.of(), tempFile));

        TestAssertions.assertEquals("Seeded catalog with 2 books and 1 member.", output, "Seed should report created sample data.");
        TestAssertions.assertTrue(Files.exists(tempFile), "Seed should create the catalog file.");
    }

    /**
     * Verifies command execution persists changes and exposes them through list commands.
     *
     * @throws IOException when file IO fails
     */
    private static void mutatesAndListsCatalog() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-state-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        service.execute(new CommandRequest(CommandName.ADD_BOOK, java.util.List.of("book-700", "Release It!", "Michael Nygard"), tempFile));
        service.execute(new CommandRequest(CommandName.ADD_MEMBER, java.util.List.of("member-700", "Drew Cole"), tempFile));
        service.execute(new CommandRequest(CommandName.CHECKOUT, java.util.List.of("book-700", "member-700"), tempFile));

        String bookListing = service.execute(new CommandRequest(CommandName.LIST_BOOKS, java.util.List.of(), tempFile));
        String memberListing = service.execute(new CommandRequest(CommandName.LIST_MEMBERS, java.util.List.of(), tempFile));

        TestAssertions.assertTrue(bookListing.contains("book-700 | Release It! | Michael Nygard | checked out"), "Book listing should reflect checkout state.");
        TestAssertions.assertTrue(memberListing.contains("member-700 | Drew Cole | book-700"), "Member listing should reflect borrowed books.");
    }
}
