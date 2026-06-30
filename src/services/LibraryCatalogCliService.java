/*
 * src/services/LibraryCatalogCliService.java
 * Executes parsed CLI commands against persisted catalog state and returns console output.
 * Connects to: src/models/CommandRequest.java, src/services/LibraryCatalogService.java, src/services/CatalogPersistenceService.java, src/cli/CatalogConsoleFormatter.java
 * Created: 2026-06-30
 */
package src.services;

import java.io.IOException;
import java.util.List;
import src.cli.CatalogConsoleFormatter;
import src.cli.CommandName;
import src.models.Book;
import src.models.CommandRequest;
import src.models.LibraryCatalogState;
import src.models.Member;

/**
 * Application service that turns parsed CLI commands into catalog operations.
 */
public final class LibraryCatalogCliService {
    private final CatalogPersistenceService persistenceService;
    private final CatalogConsoleFormatter formatter;

    /**
     * Creates the CLI service with standard dependencies.
     */
    public LibraryCatalogCliService() {
        this(new CatalogPersistenceService(), new CatalogConsoleFormatter());
    }

    /**
     * Creates the CLI service with injected dependencies.
     *
     * @param persistenceService catalog persistence implementation
     * @param formatter output formatter
     */
    public LibraryCatalogCliService(CatalogPersistenceService persistenceService, CatalogConsoleFormatter formatter) {
        if (persistenceService == null) {
            throw new IllegalArgumentException("Persistence service is required.");
        }

        if (formatter == null) {
            throw new IllegalArgumentException("Formatter is required.");
        }

        this.persistenceService = persistenceService;
        this.formatter = formatter;
    }

    /**
     * Executes a parsed command against the catalog.
     *
     * @param request parsed command request
     * @return console output
     * @throws IOException when persistence fails
     */
    public String execute(CommandRequest request) throws IOException {
        validateRequest(request);

        if (request.commandName() == CommandName.HELP) {
            return formatter.formatHelp(request.dataPath());
        }

        LibraryCatalogService catalogService = new LibraryCatalogService();
        LibraryCatalogState loadedState = persistenceService.load(request.dataPath());
        catalogService.loadState(loadedState);

        return switch (request.commandName()) {
            case HELP -> formatter.formatHelp(request.dataPath());
            case SEED -> executeSeed(catalogService, request);
            case ADD_BOOK -> executeAddBook(catalogService, request);
            case ADD_MEMBER -> executeAddMember(catalogService, request);
            case CHECKOUT -> executeCheckout(catalogService, request);
            case RETURN -> executeReturn(catalogService, request);
            case LIST_BOOKS -> formatter.formatBooks(catalogService.getBooks());
            case LIST_MEMBERS -> formatter.formatMembers(catalogService.getMembers());
            case FIND_BOOK -> executeFindBook(catalogService, request);
            case FIND_MEMBER -> executeFindMember(catalogService, request);
            case LOAN_REPORT -> formatter.formatLoanReport(catalogService.getActiveLoans());
        };
    }

    /**
     * Seeds sample data when the catalog is empty.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return success message
     * @throws IOException when persistence fails
     */
    private String executeSeed(LibraryCatalogService catalogService, CommandRequest request) throws IOException {
        if (!catalogService.getBooks().isEmpty() || !catalogService.getMembers().isEmpty()) {
            throw new IllegalStateException("Seed command requires an empty catalog file.");
        }

        catalogService.addBook(new Book("book-001", "The Pragmatic Programmer", "Andrew Hunt and David Thomas"));
        catalogService.addBook(new Book("book-002", "Clean Code", "Robert C. Martin"));
        catalogService.addMember(new Member("member-001", "Avery Stone"));

        persistState(catalogService, request);
        return "Seeded catalog with 2 books and 1 member.";
    }

    /**
     * Adds a book and persists the catalog.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return success message
     * @throws IOException when persistence fails
     */
    private String executeAddBook(LibraryCatalogService catalogService, CommandRequest request) throws IOException {
        List<String> arguments = request.arguments();
        Book book = new Book(arguments.get(0), arguments.get(1), arguments.get(2));
        catalogService.addBook(book);
        persistState(catalogService, request);
        return "Added book " + book.getId() + ".";
    }

    /**
     * Adds a member and persists the catalog.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return success message
     * @throws IOException when persistence fails
     */
    private String executeAddMember(LibraryCatalogService catalogService, CommandRequest request) throws IOException {
        List<String> arguments = request.arguments();
        Member member = new Member(arguments.get(0), arguments.get(1));
        catalogService.addMember(member);
        persistState(catalogService, request);
        return "Added member " + member.getId() + ".";
    }

    /**
     * Checks a book out and persists the catalog.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return success message
     * @throws IOException when persistence fails
     */
    private String executeCheckout(LibraryCatalogService catalogService, CommandRequest request) throws IOException {
        List<String> arguments = request.arguments();
        catalogService.checkoutBook(arguments.get(0), arguments.get(1));
        persistState(catalogService, request);
        return "Checked out book " + arguments.get(0) + " to member " + arguments.get(1) + ".";
    }

    /**
     * Returns a book and persists the catalog.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return success message
     * @throws IOException when persistence fails
     */
    private String executeReturn(LibraryCatalogService catalogService, CommandRequest request) throws IOException {
        List<String> arguments = request.arguments();
        catalogService.returnBook(arguments.get(0), arguments.get(1));
        persistState(catalogService, request);
        return "Returned book " + arguments.get(0) + " from member " + arguments.get(1) + ".";
    }

    /**
     * Persists current catalog state.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @throws IOException when persistence fails
     */
    private void persistState(LibraryCatalogService catalogService, CommandRequest request) throws IOException {
        persistenceService.save(catalogService.toState(), request.dataPath());
    }

    /**
     * Searches books by id, title, or author.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return formatted search results
     */
    private String executeFindBook(LibraryCatalogService catalogService, CommandRequest request) {
        String query = request.arguments().getFirst();
        return formatter.formatBookSearchResults(catalogService.findBooks(query), query);
    }

    /**
     * Searches members by id or name.
     *
     * @param catalogService target catalog
     * @param request parsed request
     * @return formatted search results
     */
    private String executeFindMember(LibraryCatalogService catalogService, CommandRequest request) {
        String query = request.arguments().getFirst();
        return formatter.formatMemberSearchResults(catalogService.findMembers(query), query);
    }

    /**
     * Rejects invalid requests.
     *
     * @param request parsed request
     */
    private void validateRequest(CommandRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Command request is required.");
        }
    }
}
