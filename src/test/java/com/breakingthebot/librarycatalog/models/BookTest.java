/*
 * src/test/java/models/BookTest.java
 * Verifies book validation and checkout state transitions with JUnit 5.
 * Connects to: src/main/java/models/Book.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.breakingthebot.librarycatalog.models.Book;

/**
 * Tests the Book model behavior.
 */
public final class BookTest {
    /**
     * Verifies default book state.
     */
    @Test
    void createsAvailableBookByDefault() {
        Book book = new Book("book-100", "Domain-Driven Design", "Eric Evans");

        assertFalse(book.isCheckedOut(), "New books should start as available.");
    }

    /**
     * Verifies state changes for checkout and return.
     */
    @Test
    void updatesCheckoutState() {
        Book book = new Book("book-101", "Refactoring", "Martin Fowler");
        book.checkout();
        assertTrue(book.isCheckedOut(), "Checkout should mark the book unavailable.");
        book.checkin();
        assertFalse(book.isCheckedOut(), "Checkin should mark the book available.");
    }

    /**
     * Verifies input validation for blank titles.
     */
    @Test
    void rejectsBlankTitle() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Book("book-102", " ", "Author"),
            "Book construction should reject blank titles."
        );
    }
}

