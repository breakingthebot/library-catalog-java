/*
 * src/models/Book.java
 * Represents a catalog book and its checkout state.
 * Connects to: src/services/LibraryCatalogService.java, src/services/CatalogPersistenceService.java
 * Created: 2026-06-30
 */
package src.models;

/**
 * Represents a single book in the library catalog.
 */
public final class Book {
    private final String id;
    private final String title;
    private final String author;
    private boolean checkedOut;

    /**
     * Creates a book with an available checkout state.
     *
     * @param id unique book identifier
     * @param title book title
     * @param author book author
     */
    public Book(String id, String title, String author) {
        this(id, title, author, false);
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
        this.id = validateText(id, "book id");
        this.title = validateText(title, "book title");
        this.author = validateText(author, "book author");
        this.checkedOut = checkedOut;
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
     * Marks the book as checked out.
     */
    public void checkout() {
        checkedOut = true;
    }

    /**
     * Marks the book as returned.
     */
    public void checkin() {
        checkedOut = false;
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
