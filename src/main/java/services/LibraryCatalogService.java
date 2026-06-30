/*
 * src/services/LibraryCatalogService.java
 * Coordinates book registration, member registration, and checkout rules.
 * Connects to: src/models/Book.java, src/models/Member.java, src/models/LibraryCatalogState.java
 * Created: 2026-06-30
 */
package src.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import src.models.Book;
import src.models.LibraryCatalogState;
import src.models.LoanRecord;
import src.models.Member;
import src.utils.TextSearchMatcher;

/**
 * In-memory catalog service that enforces checkout rules.
 */
public final class LibraryCatalogService {
    private static final Logger LOGGER = Logger.getLogger(LibraryCatalogService.class.getName());

    private final Map<String, Book> booksById = new LinkedHashMap<>();
    private final Map<String, Member> membersById = new LinkedHashMap<>();

    /**
     * Registers a new book in the catalog.
     *
     * @param book book to add
     */
    public void addBook(Book book) {
        Book validatedBook = validateBook(book);

        if (booksById.containsKey(validatedBook.getId())) {
            throw new IllegalArgumentException("Cannot add duplicate book id: " + validatedBook.getId());
        }

        booksById.put(validatedBook.getId(), validatedBook);
        LOGGER.log(Level.INFO, "Registered book {0}", validatedBook.getId());
    }

    /**
     * Registers a new member.
     *
     * @param member member to add
     */
    public void addMember(Member member) {
        Member validatedMember = validateMember(member);

        if (membersById.containsKey(validatedMember.getId())) {
            throw new IllegalArgumentException("Cannot add duplicate member id: " + validatedMember.getId());
        }

        membersById.put(validatedMember.getId(), validatedMember);
        LOGGER.log(Level.INFO, "Registered member {0}", validatedMember.getId());
    }

    /**
     * Checks a book out to a member when both exist and the book is available.
     *
     * @param bookId book identifier
     * @param memberId member identifier
     */
    public void checkoutBook(String bookId, String memberId) {
        Book book = requireBook(bookId);
        Member member = requireMember(memberId);

        if (book.isCheckedOut()) {
            throw new IllegalStateException("Cannot checkout book " + bookId + ": book is already checked out.");
        }

        book.checkout();
        member.borrowBook(bookId);
        LOGGER.log(Level.INFO, "Checked out book {0} to member {1}", new Object[] {bookId, memberId});
    }

    /**
     * Returns a book from a member when the loan relationship exists.
     *
     * @param bookId book identifier
     * @param memberId member identifier
     */
    public void returnBook(String bookId, String memberId) {
        Book book = requireBook(bookId);
        Member member = requireMember(memberId);

        if (!member.hasBorrowedBook(bookId)) {
            throw new IllegalStateException(
                "Cannot return book " + bookId + ": member " + memberId + " does not hold it."
            );
        }

        book.checkin();
        member.returnBook(bookId);
        LOGGER.log(Level.INFO, "Returned book {0} from member {1}", new Object[] {bookId, memberId});
    }

    /**
     * Finds a book by identifier.
     *
     * @param bookId book identifier
     * @return matching book when present
     */
    public Optional<Book> findBook(String bookId) {
        return Optional.ofNullable(booksById.get(bookId));
    }

    /**
     * Finds a member by identifier.
     *
     * @param memberId member identifier
     * @return matching member when present
     */
    public Optional<Member> findMember(String memberId) {
        return Optional.ofNullable(membersById.get(memberId));
    }

    /**
     * Returns all books in registration order.
     *
     * @return catalog books
     */
    public Collection<Book> getBooks() {
        return booksById.values();
    }

    /**
     * Returns all members in registration order.
     *
     * @return catalog members
     */
    public Collection<Member> getMembers() {
        return membersById.values();
    }

    /**
     * Finds books whose id, title, or author contains the supplied query.
     *
     * @param query search query
     * @return matching books in registration order
     */
    public List<Book> findBooks(String query) {
        validateQuery(query);

        List<Book> matches = new ArrayList<>();

        for (Book book : booksById.values()) {
            if (
                TextSearchMatcher.containsIgnoreCase(book.getId(), query)
                    || TextSearchMatcher.containsIgnoreCase(book.getTitle(), query)
                    || TextSearchMatcher.containsIgnoreCase(book.getAuthor(), query)
            ) {
                matches.add(book);
            }
        }

        return matches;
    }

    /**
     * Finds members whose id or name contains the supplied query.
     *
     * @param query search query
     * @return matching members in registration order
     */
    public List<Member> findMembers(String query) {
        validateQuery(query);

        List<Member> matches = new ArrayList<>();

        for (Member member : membersById.values()) {
            if (
                TextSearchMatcher.containsIgnoreCase(member.getId(), query)
                    || TextSearchMatcher.containsIgnoreCase(member.getName(), query)
            ) {
                matches.add(member);
            }
        }

        return matches;
    }

    /**
     * Returns active loans derived from the current checked-out books and members.
     *
     * @return active loan records in book registration order
     */
    public List<LoanRecord> getActiveLoans() {
        List<LoanRecord> loans = new ArrayList<>();

        for (Book book : booksById.values()) {
            if (!book.isCheckedOut()) {
                continue;
            }

            loans.add(buildLoanRecord(book));
        }

        return loans;
    }

    /**
     * Produces a persistence-safe snapshot of the current state.
     *
     * @return state snapshot
     */
    public LibraryCatalogState toState() {
        return new LibraryCatalogState(new ArrayList<>(booksById.values()), new ArrayList<>(membersById.values()));
    }

    /**
     * Replaces the current in-memory state with a loaded snapshot.
     *
     * @param state snapshot to load
     */
    public void loadState(LibraryCatalogState state) {
        validateState(state);

        booksById.clear();
        membersById.clear();

        for (Book book : state.books()) {
            booksById.put(book.getId(), book);
        }

        for (Member member : state.members()) {
            membersById.put(member.getId(), member);
        }

        LOGGER.log(
            Level.INFO,
            "Loaded catalog state with {0} books and {1} members",
            new Object[] {booksById.size(), membersById.size()}
        );
    }

    /**
     * Resolves a known book or fails loudly.
     *
     * @param bookId book identifier
     * @return resolved book
     */
    private Book requireBook(String bookId) {
        Book book = booksById.get(bookId);

        if (book == null) {
            throw new IllegalArgumentException("Unknown book id: " + bookId);
        }

        return book;
    }

    /**
     * Resolves a known member or fails loudly.
     *
     * @param memberId member identifier
     * @return resolved member
     */
    private Member requireMember(String memberId) {
        Member member = membersById.get(memberId);

        if (member == null) {
            throw new IllegalArgumentException("Unknown member id: " + memberId);
        }

        return member;
    }

    /**
     * Rejects invalid book inputs.
     *
     * @param book input book
     * @return validated book
     */
    private Book validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book is required.");
        }

        return book;
    }

    /**
     * Rejects invalid member inputs.
     *
     * @param member input member
     * @return validated member
     */
    private Member validateMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member is required.");
        }

        return member;
    }

    /**
     * Rejects invalid state loads.
     *
     * @param state loaded state
     */
    private void validateState(LibraryCatalogState state) {
        if (state == null) {
            throw new IllegalArgumentException("Catalog state is required.");
        }
    }

    /**
     * Rejects blank search queries.
     *
     * @param query search query
     */
    private void validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query is required.");
        }
    }

    /**
     * Builds a loan record for a checked-out book.
     *
     * @param book checked-out book
     * @return derived loan record
     */
    private LoanRecord buildLoanRecord(Book book) {
        for (Member member : membersById.values()) {
            if (member.hasBorrowedBook(book.getId())) {
                return new LoanRecord(book.getId(), book.getTitle(), member.getId(), member.getName());
            }
        }

        throw new IllegalStateException("Checked out book " + book.getId() + " is not assigned to a member.");
    }
}
