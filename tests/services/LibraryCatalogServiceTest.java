/*
 * tests/services/LibraryCatalogServiceTest.java
 * Verifies checkout rules and member-book associations.
 * Connects to: src/services/LibraryCatalogService.java, src/models/*.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.services;

import src.models.Book;
import src.models.Member;
import src.services.LibraryCatalogService;
import tests.utils.TestAssertions;

/**
 * Tests in-memory catalog behavior.
 */
public final class LibraryCatalogServiceTest {
    private LibraryCatalogServiceTest() {
    }

    /**
     * Runs all catalog service tests.
     */
    public static void runAll() {
        checksOutAndReturnsBooks();
        rejectsDuplicateBookIds();
        rejectsCheckoutOfUnavailableBook();
    }

    /**
     * Verifies the happy path for checkout and return.
     */
    private static void checksOutAndReturnsBooks() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-301", "Working Effectively with Legacy Code", "Michael Feathers"));
        service.addMember(new Member("member-301", "Harper Lane"));

        service.checkoutBook("book-301", "member-301");
        TestAssertions.assertTrue(
            service.findBook("book-301").orElseThrow().isCheckedOut(),
            "Checked out books should be unavailable."
        );
        TestAssertions.assertTrue(
            service.findMember("member-301").orElseThrow().hasBorrowedBook("book-301"),
            "Member loans should be tracked."
        );

        service.returnBook("book-301", "member-301");
        TestAssertions.assertFalse(
            service.findBook("book-301").orElseThrow().isCheckedOut(),
            "Returned books should be available again."
        );
    }

    /**
     * Verifies duplicate books are rejected.
     */
    private static void rejectsDuplicateBookIds() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-302", "Design Patterns", "Gamma et al."));

        TestAssertions.assertThrows(
            () -> service.addBook(new Book("book-302", "Design Patterns", "Gamma et al.")),
            IllegalArgumentException.class,
            "Duplicate book ids should be rejected."
        );
    }

    /**
     * Verifies unavailable books cannot be checked out twice.
     */
    private static void rejectsCheckoutOfUnavailableBook() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-303", "Effective Java", "Joshua Bloch"));
        service.addMember(new Member("member-303", "Riley Fox"));
        service.addMember(new Member("member-304", "Casey Drew"));
        service.checkoutBook("book-303", "member-303");

        TestAssertions.assertThrows(
            () -> service.checkoutBook("book-303", "member-304"),
            IllegalStateException.class,
            "Already checked out books should not be loaned twice."
        );
    }
}
