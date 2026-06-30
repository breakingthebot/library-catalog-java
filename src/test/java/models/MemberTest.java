/*
 * src/test/java/models/MemberTest.java
 * Verifies member borrowing behavior and input validation with JUnit 5.
 * Connects to: src/main/java/models/Member.java
 * Created: 2026-06-30
 */
package tests.models;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import src.models.Member;

/**
 * Tests the Member model behavior.
 */
public final class MemberTest {
    /**
     * Verifies borrow and return tracking.
     */
    @Test
    void tracksBorrowedBooks() {
        Member member = new Member("member-100", "Jordan Reed");
        member.borrowBook("book-201");
        assertTrue(member.hasBorrowedBook("book-201"), "Borrowed books should be tracked.");
        member.returnBook("book-201");
        assertFalse(member.hasBorrowedBook("book-201"), "Returned books should be removed.");
    }

    /**
     * Verifies member validation.
     */
    @Test
    void rejectsBlankMemberId() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Member(" ", "Jordan Reed"),
            "Member construction should reject blank ids."
        );
    }
}
