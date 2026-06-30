/*
 * src/models/LoanRecord.java
 * Represents an active loan between a checked-out book and the member holding it.
 * Connects to: src/services/LibraryCatalogService.java, src/cli/CatalogConsoleFormatter.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.models;

import java.time.LocalDate;

/**
 * Immutable read model for active catalog loans.
 *
 * @param bookId book identifier
 * @param bookTitle book title
 * @param memberId borrowing member identifier
 * @param memberName borrowing member name
 * @param dueDate active loan due date
 * @param overdue whether the loan is overdue
 */
public record LoanRecord(
    String bookId,
    String bookTitle,
    String memberId,
    String memberName,
    LocalDate dueDate,
    boolean overdue
) {
    /**
     * Validates required loan fields.
     *
     * @param bookId book identifier
     * @param bookTitle book title
     * @param memberId borrowing member identifier
     * @param memberName borrowing member name
     * @param dueDate active loan due date
     * @param overdue whether the loan is overdue
     */
    public LoanRecord {
        bookId = requireValue(bookId, "book id");
        bookTitle = requireValue(bookTitle, "book title");
        memberId = requireValue(memberId, "member id");
        memberName = requireValue(memberName, "member name");

        if (dueDate == null) {
            throw new IllegalArgumentException("Invalid due date: value is required.");
        }
    }

    /**
     * Validates required text fields.
     *
     * @param value input text
     * @param label field label for error messages
     * @return trimmed text
     */
    private static String requireValue(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid " + label + ": value is required.");
        }

        return value.trim();
    }
}

