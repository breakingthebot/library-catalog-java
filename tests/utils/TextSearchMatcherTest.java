/*
 * tests/utils/TextSearchMatcherTest.java
 * Verifies case-insensitive text matching for catalog search commands.
 * Connects to: src/utils/TextSearchMatcher.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.utils;

import src.utils.TextSearchMatcher;

/**
 * Tests search text matching behavior.
 */
public final class TextSearchMatcherTest {
    private TextSearchMatcherTest() {
    }

    /**
     * Runs all text matcher tests.
     */
    public static void runAll() {
        matchesIgnoringCase();
        rejectsBlankQueries();
    }

    /**
     * Verifies case-insensitive substring matching.
     */
    private static void matchesIgnoringCase() {
        TestAssertions.assertTrue(
            TextSearchMatcher.containsIgnoreCase("The Pragmatic Programmer", "pragmatic"),
            "Search matcher should ignore case."
        );
    }

    /**
     * Verifies blank queries fail validation.
     */
    private static void rejectsBlankQueries() {
        TestAssertions.assertThrows(
            () -> TextSearchMatcher.containsIgnoreCase("Clean Code", " "),
            IllegalArgumentException.class,
            "Blank search queries should be rejected."
        );
    }
}
