/*
 * tests/models/BookTest.java
 * Verifies book validation and checkout state transitions.
 * Connects to: src/models/Book.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.models;

import src.models.Book;
import tests.utils.TestAssertions;

/**
 * Tests the Book model behavior.
 */
public final class BookTest {
    private BookTest() {
    }

    /**
     * Runs all book tests.
     */
    public static void runAll() {
        createsAvailableBookByDefault();
        updatesCheckoutState();
        rejectsBlankTitle();
    }

    /**
     * Verifies default book state.
     */
    private static void createsAvailableBookByDefault() {
        Book book = new Book("book-100", "Domain-Driven Design", "Eric Evans");

        TestAssertions.assertFalse(book.isCheckedOut(), "New books should start as available.");
    }

    /**
     * Verifies state changes for checkout and return.
     */
    private static void updatesCheckoutState() {
        Book book = new Book("book-101", "Refactoring", "Martin Fowler");
        book.checkout();
        TestAssertions.assertTrue(book.isCheckedOut(), "Checkout should mark the book unavailable.");
        book.checkin();
        TestAssertions.assertFalse(book.isCheckedOut(), "Checkin should mark the book available.");
    }

    /**
     * Verifies input validation for blank titles.
     */
    private static void rejectsBlankTitle() {
        TestAssertions.assertThrows(
            () -> new Book("book-102", " ", "Author"),
            IllegalArgumentException.class,
            "Book construction should reject blank titles."
        );
    }
}
