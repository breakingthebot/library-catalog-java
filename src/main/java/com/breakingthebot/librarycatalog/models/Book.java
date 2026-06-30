/*
 * src/models/Book.java
 * Represents a catalog book and its checkout state.
 * Connects to: src/services/LibraryCatalogService.java, src/services/CatalogPersistenceService.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.models;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a single book in the library catalog.
 */
public final class Book {
    private final String id;
    private final String title;
    private final String author;
    private boolean checkedOut;
    private LocalDate dueDate;

    /**
     * Creates a book with an available checkout state.
     *
     * @param id unique book identifier
     * @param title book title
     * @param author book author
     */
    public Book(String id, String title, String author) {
        this(id, title, author, false, null);
    }

    /**
     * Creates a book with an explicit checkout state.
     *
     * @param id unique book identifier
     * @param title book title
     * @param author book author
     * @param checkedOut whether the book is already checked out
     */
    public Book(String id, String title, String author, boolean checkedOut) {
        this(id, title, author, checkedOut, null);
    }

    /**
     * Creates a book with an explicit checkout state and optional due date.
     *
     * @param id unique book identifier
     * @param title book title
     * @param author book author
     * @param checkedOut whether the book is already checked out
     * @param dueDate due date for the active loan when present
     */
    public Book(String id, String title, String author, boolean checkedOut, LocalDate dueDate) {
        this.id = validateText(id, "book id");
        this.title = validateText(title, "book title");
        this.author = validateText(author, "book author");
        this.checkedOut = checkedOut;
        this.dueDate = dueDate;
    }

    /**
     * Returns the book identifier.
     *
     * @return book identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the author.
     *
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Indicates whether the book is checked out.
     *
     * @return true when unavailable
     */
    public boolean isCheckedOut() {
        return checkedOut;
    }

    /**
     * Returns the due date for an active loan when present.
     *
     * @return due date for the active loan
     */
    public Optional<LocalDate> getDueDate() {
        return Optional.ofNullable(dueDate);
    }

    /**
     * Marks the book as checked out with a due date.
     *
     * @param dueDate due date for the new loan
     */
    public void checkout(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("Loan due date is required.");
        }

        checkedOut = true;
        this.dueDate = dueDate;
    }

    /**
     * Marks the book as returned.
     */
    public void checkin() {
        checkedOut = false;
        dueDate = null;
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

