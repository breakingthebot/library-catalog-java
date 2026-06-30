/*
 * src/test/java/utils/TextSearchMatcherTest.java
 * Verifies case-insensitive text matching for catalog search commands with JUnit 5.
 * Connects to: src/main/java/utils/TextSearchMatcher.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.utils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.breakingthebot.librarycatalog.utils.TextSearchMatcher;

/**
 * Tests search text matching behavior.
 */
public final class TextSearchMatcherTest {
    /**
     * Verifies case-insensitive substring matching.
     */
    @Test
    void matchesIgnoringCase() {
        assertTrue(
            TextSearchMatcher.containsIgnoreCase("The Pragmatic Programmer", "pragmatic"),
            "Search matcher should ignore case."
        );
    }

    /**
     * Verifies blank queries fail validation.
     */
    @Test
    void rejectsBlankQueries() {
        assertThrows(
            IllegalArgumentException.class,
            () -> TextSearchMatcher.containsIgnoreCase("Clean Code", " "),
            "Blank search queries should be rejected."
        );
    }
}

