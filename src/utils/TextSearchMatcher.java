/*
 * src/utils/TextSearchMatcher.java
 * Provides case-insensitive containment checks for catalog search commands.
 * Connects to: src/services/LibraryCatalogService.java
 * Created: 2026-06-30
 */
package src.utils;

/**
 * Shared text-matching helper for simple catalog searches.
 */
public final class TextSearchMatcher {
    private TextSearchMatcher() {
    }

    /**
     * Returns true when the candidate contains the query, ignoring case.
     *
     * @param candidate searchable text
     * @param query user-provided query
     * @return true when the query matches the candidate
     */
    public static boolean containsIgnoreCase(String candidate, String query) {
        if (candidate == null || query == null) {
            throw new IllegalArgumentException("Candidate text and query are required.");
        }

        String normalizedCandidate = candidate.trim().toLowerCase();
        String normalizedQuery = query.trim().toLowerCase();

        if (normalizedQuery.isEmpty()) {
            throw new IllegalArgumentException("Search query is required.");
        }

        return normalizedCandidate.contains(normalizedQuery);
    }
}
