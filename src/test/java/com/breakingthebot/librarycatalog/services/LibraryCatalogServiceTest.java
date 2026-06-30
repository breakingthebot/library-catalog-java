/*
 * src/test/java/services/LibraryCatalogServiceTest.java
 * Verifies checkout rules, search behavior, and loan reporting with JUnit 5.
 * Connects to: src/main/java/services/LibraryCatalogService.java, src/main/java/models/*.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.breakingthebot.librarycatalog.models.Book;
import com.breakingthebot.librarycatalog.models.Member;
import com.breakingthebot.librarycatalog.services.LibraryCatalogService;

/**
 * Tests in-memory catalog behavior.
 */
public final class LibraryCatalogServiceTest {
    /**
     * Verifies the happy path for checkout and return.
     */
    @Test
    void checksOutAndReturnsBooks() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-301", "Working Effectively with Legacy Code", "Michael Feathers"));
        service.addMember(new Member("member-301", "Harper Lane"));

        service.checkoutBook("book-301", "member-301");
        assertTrue(
            service.findBook("book-301").orElseThrow().isCheckedOut(),
            "Checked out books should be unavailable."
        );
        assertTrue(
            service.findMember("member-301").orElseThrow().hasBorrowedBook("book-301"),
            "Member loans should be tracked."
        );

        service.returnBook("book-301", "member-301");
        assertFalse(
            service.findBook("book-301").orElseThrow().isCheckedOut(),
            "Returned books should be available again."
        );
    }

    /**
     * Verifies duplicate books are rejected.
     */
    @Test
    void rejectsDuplicateBookIds() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-302", "Design Patterns", "Gamma et al."));

        assertThrows(
            IllegalArgumentException.class,
            () -> service.addBook(new Book("book-302", "Design Patterns", "Gamma et al.")),
            "Duplicate book ids should be rejected."
        );
    }

    /**
     * Verifies unavailable books cannot be checked out twice.
     */
    @Test
    void rejectsCheckoutOfUnavailableBook() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-303", "Effective Java", "Joshua Bloch"));
        service.addMember(new Member("member-303", "Riley Fox"));
        service.addMember(new Member("member-304", "Casey Drew"));
        service.checkoutBook("book-303", "member-303");

        assertThrows(
            IllegalStateException.class,
            () -> service.checkoutBook("book-303", "member-304"),
            "Already checked out books should not be loaned twice."
        );
    }

    /**
     * Verifies partial and case-insensitive search behavior.
     */
    @Test
    void findsBooksAndMembersByPartialMatch() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-304", "Domain-Driven Design", "Eric Evans"));
        service.addBook(new Book("book-305", "Refactoring", "Martin Fowler"));
        service.addMember(new Member("member-305", "Jamie Cross"));

        assertEquals(1, service.findBooks("eric").size(), "Author search should find matching books.");
        assertEquals(1, service.findBooks("domain").size(), "Title search should find matching books.");
        assertEquals(1, service.findMembers("jamie").size(), "Name search should find matching members.");
    }

    /**
     * Verifies active loan records are derived from current checkout state.
     */
    @Test
    void returnsActiveLoans() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-306", "Clean Architecture", "Robert C. Martin"));
        service.addBook(new Book("book-307", "Patterns of Enterprise Application Architecture", "Martin Fowler"));
        service.addMember(new Member("member-306", "Morgan Tate"));
        service.checkoutBook("book-306", "member-306");

        assertEquals(1, service.getActiveLoans().size(), "Only checked-out books should appear in the loan report.");
        assertEquals("member-306", service.getActiveLoans().getFirst().memberId(), "Loan report should include the borrower.");
    }
}

