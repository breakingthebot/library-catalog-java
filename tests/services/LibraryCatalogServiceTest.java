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
        findsBooksAndMembersByPartialMatch();
        returnsActiveLoans();
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

    /**
     * Verifies partial and case-insensitive search behavior.
     */
    private static void findsBooksAndMembersByPartialMatch() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-304", "Domain-Driven Design", "Eric Evans"));
        service.addBook(new Book("book-305", "Refactoring", "Martin Fowler"));
        service.addMember(new Member("member-305", "Jamie Cross"));

        TestAssertions.assertEquals(1, service.findBooks("eric").size(), "Author search should find matching books.");
        TestAssertions.assertEquals(1, service.findBooks("domain").size(), "Title search should find matching books.");
        TestAssertions.assertEquals(1, service.findMembers("jamie").size(), "Name search should find matching members.");
    }

    /**
     * Verifies active loan records are derived from current checkout state.
     */
    private static void returnsActiveLoans() {
        LibraryCatalogService service = new LibraryCatalogService();
        service.addBook(new Book("book-306", "Clean Architecture", "Robert C. Martin"));
        service.addBook(new Book("book-307", "Patterns of Enterprise Application Architecture", "Martin Fowler"));
        service.addMember(new Member("member-306", "Morgan Tate"));
        service.checkoutBook("book-306", "member-306");

        TestAssertions.assertEquals(1, service.getActiveLoans().size(), "Only checked-out books should appear in the loan report.");
        TestAssertions.assertEquals("member-306", service.getActiveLoans().getFirst().memberId(), "Loan report should include the borrower.");
    }
}
