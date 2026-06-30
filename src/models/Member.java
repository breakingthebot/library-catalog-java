/*
 * src/models/Member.java
 * Represents a library member and the books they currently hold.
 * Connects to: src/services/LibraryCatalogService.java, src/services/CatalogPersistenceService.java
 * Created: 2026-06-30
 */
package src.models;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a member who can borrow books.
 */
public final class Member {
    private final String id;
    private final String name;
    private final Set<String> borrowedBookIds;

    /**
     * Creates a member with no borrowed books.
     *
     * @param id unique member identifier
     * @param name member display name
     */
    public Member(String id, String name) {
        this(id, name, Set.of());
    }

    /**
     * Creates a member with an existing borrowed-book state.
     *
     * @param id unique member identifier
     * @param name member display name
     * @param borrowedBookIds borrowed book identifiers
     */
    public Member(String id, String name, Set<String> borrowedBookIds) {
        this.id = validateText(id, "member id");
        this.name = validateText(name, "member name");
        this.borrowedBookIds = new LinkedHashSet<>(borrowedBookIds);
    }

    /**
     * Returns the member identifier.
     *
     * @return member identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the member name.
     *
     * @return member name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an immutable view of borrowed book identifiers.
     *
     * @return borrowed book identifiers
     */
    public Set<String> getBorrowedBookIds() {
        return Collections.unmodifiableSet(borrowedBookIds);
    }

    /**
     * Adds a book to the borrowed set.
     *
     * @param bookId borrowed book identifier
     */
    public void borrowBook(String bookId) {
        borrowedBookIds.add(validateText(bookId, "book id"));
    }

    /**
     * Removes a book from the borrowed set.
     *
     * @param bookId returned book identifier
     */
    public void returnBook(String bookId) {
        borrowedBookIds.remove(validateText(bookId, "book id"));
    }

    /**
     * Indicates whether the member currently holds the book.
     *
     * @param bookId book identifier
     * @return true when the book is borrowed by this member
     */
    public boolean hasBorrowedBook(String bookId) {
        return borrowedBookIds.contains(validateText(bookId, "book id"));
    }

    /**
     * Validates non-empty text fields.
     *
     * @param value input text
     * @param fieldName field label for error messages
     * @return trimmed text
     */
    private static String validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": value is required.");
        }

        return value.trim();
    }
}
