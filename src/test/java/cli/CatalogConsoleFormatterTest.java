/*
 * src/test/java/cli/CatalogConsoleFormatterTest.java
 * Verifies the user-facing output generated for help and catalog listings with JUnit 5.
 * Connects to: src/main/java/cli/CatalogConsoleFormatter.java, src/main/java/models/*.java
 * Created: 2026-06-30
 */
package tests.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import src.cli.CatalogConsoleFormatter;
import src.models.Book;
import src.models.LoanRecord;
import src.models.Member;

/**
 * Tests console formatting behavior.
 */
public final class CatalogConsoleFormatterTest {
    /**
     * Verifies help output contains the expected commands.
     */
    @Test
    void rendersHelpText() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatHelp(Path.of("data/catalog.txt"));

        assertTrue(output.contains("add-book <book-id> <title> <author>"), "Help should list book creation.");
        assertTrue(output.contains("--data <path>"), "Help should describe the data flag.");
    }

    /**
     * Verifies book listing output.
     */
    @Test
    void rendersBookListings() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatBooks(List.of(new Book("book-600", "DDD", "Eric Evans", true)));

        assertTrue(output.contains("book-600 | DDD | Eric Evans | checked out"), "Books should render in one line.");
    }

    /**
     * Verifies member listing output.
     */
    @Test
    void rendersMemberListings() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatMembers(List.of(new Member("member-600", "Alex Kim", Set.of("book-600"))));

        assertTrue(output.contains("member-600 | Alex Kim | book-600"), "Members should include borrowed book ids.");
    }

    /**
     * Verifies search no-match output.
     */
    @Test
    void rendersEmptySearchResults() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();

        assertEquals(
            "No books matched query: missing",
            formatter.formatBookSearchResults(List.of(), "missing"),
            "Book searches should explain when nothing matches."
        );
    }

    /**
     * Verifies loan report formatting.
     */
    @Test
    void rendersLoanReport() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatLoanReport(List.of(new LoanRecord("book-610", "DDD", "member-610", "Jamie Cross")));

        assertTrue(output.contains("book-610 | DDD | member-610 | Jamie Cross"), "Loan reports should include book and member details.");
    }
}
