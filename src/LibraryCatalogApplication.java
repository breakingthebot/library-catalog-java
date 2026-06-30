/*
 * src/LibraryCatalogApplication.java
 * Demonstrates the library catalog domain, checkout flow, and file persistence.
 * Connects to: src/config/ApplicationPaths.java, src/models/*.java, src/services/*.java
 * Created: 2026-06-30
 */
package src;

import java.io.IOException;
import java.nio.file.Path;
import src.config.ApplicationPaths;
import src.models.Book;
import src.models.LibraryCatalogState;
import src.models.Member;
import src.services.CatalogPersistenceService;
import src.services.LibraryCatalogService;

/**
 * Command-line entry point for the first catalog iteration.
 */
public final class LibraryCatalogApplication {
    private LibraryCatalogApplication() {
    }

    /**
     * Runs a small workflow and persists the result.
     *
     * @param args optional first argument overrides the output path
     * @throws IOException when persistence fails
     */
    public static void main(String[] args) throws IOException {
        Path outputPath = resolveOutputPath(args);

        LibraryCatalogService catalogService = new LibraryCatalogService();
        CatalogPersistenceService persistenceService = new CatalogPersistenceService();

        catalogService.addBook(new Book("book-001", "The Pragmatic Programmer", "Andrew Hunt and David Thomas"));
        catalogService.addBook(new Book("book-002", "Clean Code", "Robert C. Martin"));
        catalogService.addMember(new Member("member-001", "Avery Stone"));

        catalogService.checkoutBook("book-001", "member-001");

        LibraryCatalogState state = catalogService.toState();
        persistenceService.save(state, outputPath);

        LibraryCatalogState reloadedState = persistenceService.load(outputPath);
        System.out.println("Saved " + reloadedState.books().size() + " books and " + reloadedState.members().size() + " members.");
        System.out.println("Catalog file: " + outputPath.toAbsolutePath());
    }

    /**
     * Resolves the output path from CLI arguments or the default application path.
     *
     * @param args CLI arguments
     * @return output path
     */
    private static Path resolveOutputPath(String[] args) {
        if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
            return Path.of(args[0].trim());
        }

        return ApplicationPaths.DEFAULT_CATALOG_PATH;
    }
}
