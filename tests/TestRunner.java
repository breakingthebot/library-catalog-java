/*
 * tests/TestRunner.java
 * Executes the project test suite without third-party test dependencies.
 * Connects to: tests/models/*.java, tests/services/*.java, tests/utils/*.java
 * Created: 2026-06-30
 */
package tests;

import tests.models.BookTest;
import tests.models.MemberTest;
import tests.services.CatalogPersistenceServiceTest;
import tests.services.LibraryCatalogServiceTest;
import tests.utils.FieldCodecTest;

/**
 * Main entry point for the custom test suite.
 */
public final class TestRunner {
    private TestRunner() {
    }

    /**
     * Runs all project tests.
     *
     * @param args unused CLI arguments
     * @throws Exception when a test fails
     */
    public static void main(String[] args) throws Exception {
        BookTest.runAll();
        MemberTest.runAll();
        FieldCodecTest.runAll();
        LibraryCatalogServiceTest.runAll();
        CatalogPersistenceServiceTest.runAll();
        System.out.println("All tests passed.");
    }
}
