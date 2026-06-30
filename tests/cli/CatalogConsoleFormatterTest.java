/*
 * tests/cli/CatalogConsoleFormatterTest.java
 * Verifies the user-facing output generated for help and catalog listings.
 * Connects to: src/cli/CatalogConsoleFormatter.java, src/models/*.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.cli;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import src.cli.CatalogConsoleFormatter;
import src.models.Book;
import src.models.LoanRecord;
import src.models.Member;
import tests.utils.TestAssertions;

/**
 * Tests console formatting behavior.
 */
public final class CatalogConsoleFormatterTest {
    private CatalogConsoleFormatterTest() {
    }

    /**
     * Runs all formatter tests.
     */
    public static void runAll() {
        rendersHelpText();
        rendersBookListings();
        rendersMemberListings();
        rendersEmptySearchResults();
        rendersLoanReport();
    }

    /**
     * Verifies help output contains the expected commands.
     */
    private static void rendersHelpText() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatHelp(Path.of("data/catalog.txt"));

        TestAssertions.assertTrue(output.contains("add-book <book-id> <title> <author>"), "Help should list book creation.");
        TestAssertions.assertTrue(output.contains("--data <path>"), "Help should describe the data flag.");
    }

    /**
     * Verifies book listing output.
     */
    private static void rendersBookListings() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatBooks(List.of(new Book("book-600", "DDD", "Eric Evans", true)));

        TestAssertions.assertTrue(output.contains("book-600 | DDD | Eric Evans | checked out"), "Books should render in one line.");
    }

    /**
     * Verifies member listing output.
     */
    private static void rendersMemberListings() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatMembers(List.of(new Member("member-600", "Alex Kim", Set.of("book-600"))));

        TestAssertions.assertTrue(output.contains("member-600 | Alex Kim | book-600"), "Members should include borrowed book ids.");
    }

    /**
     * Verifies search no-match output.
     */
    private static void rendersEmptySearchResults() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();

        TestAssertions.assertEquals(
            "No books matched query: missing",
            formatter.formatBookSearchResults(List.of(), "missing"),
            "Book searches should explain when nothing matches."
        );
    }

    /**
     * Verifies loan report formatting.
     */
    private static void rendersLoanReport() {
        CatalogConsoleFormatter formatter = new CatalogConsoleFormatter();
        String output = formatter.formatLoanReport(List.of(new LoanRecord("book-610", "DDD", "member-610", "Jamie Cross")));

        TestAssertions.assertTrue(output.contains("book-610 | DDD | member-610 | Jamie Cross"), "Loan reports should include book and member details.");
    }
}
