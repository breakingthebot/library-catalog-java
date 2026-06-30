/*
 * tests/services/CatalogPersistenceServiceTest.java
 * Verifies the flat-file persistence layer saves and reloads catalog state.
 * Connects to: src/services/CatalogPersistenceService.java, src/models/*.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import src.models.Book;
import src.models.LibraryCatalogState;
import src.models.Member;
import src.services.CatalogPersistenceService;
import tests.utils.TestAssertions;

/**
 * Tests persistence round-trip behavior.
 */
public final class CatalogPersistenceServiceTest {
    private CatalogPersistenceServiceTest() {
    }

    /**
     * Runs all persistence tests.
     *
     * @throws IOException when filesystem setup fails
     */
    public static void runAll() throws IOException {
        savesAndLoadsCatalogState();
        returnsEmptyStateForMissingFile();
    }

    /**
     * Verifies state survives a save and load cycle.
     *
     * @throws IOException when file IO fails
     */
    private static void savesAndLoadsCatalogState() throws IOException {
        CatalogPersistenceService service = new CatalogPersistenceService();
        Path tempFile = Files.createTempFile("library-catalog-", ".txt");

        LibraryCatalogState state = new LibraryCatalogState(
            java.util.List.of(new Book("book-401", "Patterns of Enterprise Application Architecture", "Martin Fowler", true)),
            java.util.List.of(new Member("member-401", "Taylor North", java.util.Set.of("book-401")))
        );

        service.save(state, tempFile);
        LibraryCatalogState loadedState = service.load(tempFile);

        TestAssertions.assertEquals(1, loadedState.books().size(), "Saved state should reload the book list.");
        TestAssertions.assertEquals(1, loadedState.members().size(), "Saved state should reload the member list.");
        TestAssertions.assertTrue(loadedState.books().getFirst().isCheckedOut(), "Book checkout state should persist.");
        TestAssertions.assertTrue(
            loadedState.members().getFirst().hasBorrowedBook("book-401"),
            "Member loans should persist."
        );
    }

    /**
     * Verifies missing files produce an empty state.
     *
     * @throws IOException when file IO fails
     */
    private static void returnsEmptyStateForMissingFile() throws IOException {
        CatalogPersistenceService service = new CatalogPersistenceService();
        Path missingFile = Path.of(System.getProperty("java.io.tmpdir"), "missing-library-catalog-" + System.nanoTime() + ".txt");
        LibraryCatalogState loadedState = service.load(missingFile);

        TestAssertions.assertEquals(0, loadedState.books().size(), "Missing files should produce an empty book list.");
        TestAssertions.assertEquals(0, loadedState.members().size(), "Missing files should produce an empty member list.");
    }
}
