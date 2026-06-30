/*
 * src/test/java/services/LibraryCatalogCliServiceTest.java
 * Verifies the interactive CLI service executes commands against persisted catalog data with JUnit 5.
 * Connects to: src/main/java/services/LibraryCatalogCliService.java, src/main/java/cli/*.java, src/main/java/models/*.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import com.breakingthebot.librarycatalog.cli.CatalogConsoleFormatter;
import com.breakingthebot.librarycatalog.cli.CommandName;
import com.breakingthebot.librarycatalog.models.CommandRequest;
import com.breakingthebot.librarycatalog.services.CatalogPersistenceService;
import com.breakingthebot.librarycatalog.services.LibraryCatalogCliService;

/**
 * Tests the application-level CLI workflow.
 */
public final class LibraryCatalogCliServiceTest {
    /**
     * Verifies the seed command initializes a new catalog file.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void seedsEmptyCatalog() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-seed-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        String output = service.execute(new CommandRequest(CommandName.SEED, java.util.List.of(), tempFile));

        assertEquals("Seeded catalog with 2 books and 1 member.", output, "Seed should report created sample data.");
        assertTrue(Files.exists(tempFile), "Seed should create the catalog file.");
    }

    /**
     * Verifies bootstrap seeds only when the catalog file does not already exist.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void bootstrapsMissingCatalogOnlyOnce() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-bootstrap-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        String firstOutput = service.execute(new CommandRequest(CommandName.BOOTSTRAP, java.util.List.of(), tempFile));
        String secondOutput = service.execute(new CommandRequest(CommandName.BOOTSTRAP, java.util.List.of(), tempFile));
        String memberListing = service.execute(new CommandRequest(CommandName.LIST_MEMBERS, java.util.List.of(), tempFile));

        assertEquals("Bootstrapped catalog with 2 books and 1 member.", firstOutput, "Bootstrap should seed a missing catalog.");
        assertTrue(secondOutput.contains("Bootstrap skipped: catalog file already exists"), "Bootstrap should skip existing files.");
        assertTrue(memberListing.contains("member-001 | Avery Stone"), "Bootstrap should persist the sample member.");
    }

    /**
     * Verifies command execution persists changes and exposes them through list commands.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void mutatesAndListsCatalog() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-state-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        service.execute(new CommandRequest(CommandName.ADD_BOOK, java.util.List.of("book-700", "Release It!", "Michael Nygard"), tempFile));
        service.execute(new CommandRequest(CommandName.ADD_MEMBER, java.util.List.of("member-700", "Drew Cole"), tempFile));
        service.execute(new CommandRequest(CommandName.CHECKOUT, java.util.List.of("book-700", "member-700"), tempFile));

        String bookListing = service.execute(new CommandRequest(CommandName.LIST_BOOKS, java.util.List.of(), tempFile));
        String memberListing = service.execute(new CommandRequest(CommandName.LIST_MEMBERS, java.util.List.of(), tempFile));

        assertTrue(bookListing.contains("book-700 | Release It! | Michael Nygard | checked out"), "Book listing should reflect checkout state.");
        assertTrue(memberListing.contains("member-700 | Drew Cole | book-700"), "Member listing should reflect borrowed books.");
    }

    /**
     * Verifies remove commands persist deletions for available books and idle members.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void removesPersistedBookAndMember() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-remove-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        service.execute(new CommandRequest(CommandName.ADD_BOOK, java.util.List.of("book-705", "DDD Distilled", "Vaughn Vernon"), tempFile));
        service.execute(new CommandRequest(CommandName.ADD_MEMBER, java.util.List.of("member-705", "Morgan Ellis"), tempFile));

        String removedBook = service.execute(new CommandRequest(CommandName.REMOVE_BOOK, java.util.List.of("book-705"), tempFile));
        String removedMember = service.execute(new CommandRequest(CommandName.REMOVE_MEMBER, java.util.List.of("member-705"), tempFile));
        String bookListing = service.execute(new CommandRequest(CommandName.LIST_BOOKS, java.util.List.of(), tempFile));
        String memberListing = service.execute(new CommandRequest(CommandName.LIST_MEMBERS, java.util.List.of(), tempFile));

        assertEquals("Removed book book-705.", removedBook, "Remove book should report the removed id.");
        assertEquals("Removed member member-705.", removedMember, "Remove member should report the removed id.");
        assertEquals("No books found.", bookListing, "Removed books should not appear in persisted listings.");
        assertEquals("No members found.", memberListing, "Removed members should not appear in persisted listings.");
    }

    /**
     * Verifies remove commands preserve loan integrity rules.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void rejectsUnsafePersistedRemovals() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-remove-rules-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        service.execute(new CommandRequest(CommandName.ADD_BOOK, java.util.List.of("book-706", "Clean Code", "Robert C. Martin"), tempFile));
        service.execute(new CommandRequest(CommandName.ADD_MEMBER, java.util.List.of("member-706", "Skyler Reed"), tempFile));
        service.execute(new CommandRequest(CommandName.CHECKOUT, java.util.List.of("book-706", "member-706"), tempFile));

        IllegalStateException bookError = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalStateException.class,
            () -> service.execute(new CommandRequest(CommandName.REMOVE_BOOK, java.util.List.of("book-706"), tempFile)),
            "Checked-out books should not be removable through the CLI."
        );
        IllegalStateException memberError = org.junit.jupiter.api.Assertions.assertThrows(
            IllegalStateException.class,
            () -> service.execute(new CommandRequest(CommandName.REMOVE_MEMBER, java.util.List.of("member-706"), tempFile)),
            "Members with active loans should not be removable through the CLI."
        );

        assertTrue(bookError.getMessage().contains("currently checked out"), "Book removal should explain the blocking loan.");
        assertTrue(memberError.getMessage().contains("still has borrowed books"), "Member removal should explain the blocking loan.");
    }

    /**
     * Verifies search commands use persisted catalog state.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void searchesPersistedCatalog() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-search-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        service.execute(new CommandRequest(CommandName.ADD_BOOK, java.util.List.of("book-710", "Clean Architecture", "Robert C. Martin"), tempFile));
        service.execute(new CommandRequest(CommandName.ADD_MEMBER, java.util.List.of("member-710", "Casey Nguyen"), tempFile));

        String bookResult = service.execute(new CommandRequest(CommandName.FIND_BOOK, java.util.List.of("architecture"), tempFile));
        String memberResult = service.execute(new CommandRequest(CommandName.FIND_MEMBER, java.util.List.of("casey"), tempFile));
        String emptyResult = service.execute(new CommandRequest(CommandName.FIND_BOOK, java.util.List.of("missing"), tempFile));

        assertTrue(bookResult.contains("book-710 | Clean Architecture | Robert C. Martin | available"), "Book search should return matching books.");
        assertTrue(memberResult.contains("member-710 | Casey Nguyen | no borrowed books"), "Member search should return matching members.");
        assertEquals("No books matched query: missing", emptyResult, "No-match search should explain the result.");
    }

    /**
     * Verifies the loan report uses persisted checkout state.
     *
     * @throws IOException when file IO fails
     */
    @Test
    void reportsPersistedLoans() throws IOException {
        Path tempFile = Files.createTempFile("library-cli-loans-", ".txt");
        Files.deleteIfExists(tempFile);
        LibraryCatalogCliService service = new LibraryCatalogCliService(new CatalogPersistenceService(), new CatalogConsoleFormatter());

        service.execute(new CommandRequest(CommandName.ADD_BOOK, java.util.List.of("book-720", "Effective Java", "Joshua Bloch"), tempFile));
        service.execute(new CommandRequest(CommandName.ADD_MEMBER, java.util.List.of("member-720", "Taylor Stone"), tempFile));
        service.execute(new CommandRequest(CommandName.CHECKOUT, java.util.List.of("book-720", "member-720"), tempFile));

        String loanReport = service.execute(new CommandRequest(CommandName.LOAN_REPORT, java.util.List.of(), tempFile));

        assertTrue(
            loanReport.contains("book-720 | Effective Java | member-720 | Taylor Stone"),
            "Loan report should show checked-out books and the borrowing member."
        );
    }
}

