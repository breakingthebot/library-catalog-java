/*
 * tests/models/MemberTest.java
 * Verifies member borrowing behavior and input validation.
 * Connects to: src/models/Member.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.models;

import src.models.Member;
import tests.utils.TestAssertions;

/**
 * Tests the Member model behavior.
 */
public final class MemberTest {
    private MemberTest() {
    }

    /**
     * Runs all member tests.
     */
    public static void runAll() {
        tracksBorrowedBooks();
        rejectsBlankMemberId();
    }

    /**
     * Verifies borrow and return tracking.
     */
    private static void tracksBorrowedBooks() {
        Member member = new Member("member-100", "Jordan Reed");
        member.borrowBook("book-201");
        TestAssertions.assertTrue(member.hasBorrowedBook("book-201"), "Borrowed books should be tracked.");
        member.returnBook("book-201");
        TestAssertions.assertFalse(member.hasBorrowedBook("book-201"), "Returned books should be removed.");
    }

    /**
     * Verifies member validation.
     */
    private static void rejectsBlankMemberId() {
        TestAssertions.assertThrows(
            () -> new Member(" ", "Jordan Reed"),
            IllegalArgumentException.class,
            "Member construction should reject blank ids."
        );
    }
}
