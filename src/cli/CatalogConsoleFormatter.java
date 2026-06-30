/*
 * src/cli/CatalogConsoleFormatter.java
 * Formats help text and catalog listings for the command-line interface.
 * Connects to: src/services/LibraryCatalogCliService.java, src/models/Book.java, src/models/Member.java
 * Created: 2026-06-30
 */
package src.cli;

import java.nio.file.Path;
import java.util.Collection;
import java.util.StringJoiner;
import src.models.Book;
import src.models.Member;

/**
 * Creates user-facing console output for catalog commands.
 */
public final class CatalogConsoleFormatter {
    /**
     * Builds CLI help text.
     *
     * @param dataPath active data path
     * @return formatted help text
     */
    public String formatHelp(Path dataPath) {
        return String.join(
            System.lineSeparator(),
            "Library Catalog CLI",
            "Data file: " + dataPath.toAbsolutePath(),
            "Commands:",
            "  help",
            "  seed",
            "  add-book <book-id> <title> <author>",
            "  add-member <member-id> <name>",
            "  checkout <book-id> <member-id>",
            "  return <book-id> <member-id>",
            "  list-books",
            "  list-members",
            "  find-book <query>",
            "  find-member <query>",
            "Optional flag:",
            "  --data <path>  Use a custom catalog file"
        );
    }

    /**
     * Formats books for console output.
     *
     * @param books books to render
     * @return formatted listing
     */
    public String formatBooks(Collection<Book> books) {
        if (books.isEmpty()) {
            return "No books found.";
        }

        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        for (Book book : books) {
            String availability = book.isCheckedOut() ? "checked out" : "available";
            joiner.add(book.getId() + " | " + book.getTitle() + " | " + book.getAuthor() + " | " + availability);
        }

        return joiner.toString();
    }

    /**
     * Formats members for console output.
     *
     * @param members members to render
     * @return formatted listing
     */
    public String formatMembers(Collection<Member> members) {
        if (members.isEmpty()) {
            return "No members found.";
        }

        StringJoiner joiner = new StringJoiner(System.lineSeparator());

        for (Member member : members) {
            String borrowedBooks = member.getBorrowedBookIds().isEmpty()
                ? "no borrowed books"
                : String.join(", ", member.getBorrowedBookIds());
            joiner.add(member.getId() + " | " + member.getName() + " | " + borrowedBooks);
        }

        return joiner.toString();
    }

    /**
     * Formats matched books for search output.
     *
     * @param books matching books
     * @param query original search query
     * @return formatted search result
     */
    public String formatBookSearchResults(Collection<Book> books, String query) {
        if (books.isEmpty()) {
            return "No books matched query: " + query;
        }

        return formatBooks(books);
    }

    /**
     * Formats matched members for search output.
     *
     * @param members matching members
     * @param query original search query
     * @return formatted search result
     */
    public String formatMemberSearchResults(Collection<Member> members, String query) {
        if (members.isEmpty()) {
            return "No members matched query: " + query;
        }

        return formatMembers(members);
    }
}
