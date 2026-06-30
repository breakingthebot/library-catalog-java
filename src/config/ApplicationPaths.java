/*
 * src/config/ApplicationPaths.java
 * Defines default local filesystem paths used by the CLI entry point.
 * Connects to: src/LibraryCatalogApplication.java
 * Created: 2026-06-30
 */
package src.config;

import java.nio.file.Path;

/**
 * Centralizes local paths so the CLI does not scatter path literals.
 */
public final class ApplicationPaths {
    /**
     * Default file used for storing the catalog state locally.
     */
    public static final Path DEFAULT_CATALOG_PATH = Path.of("data", "library-catalog.txt");

    private ApplicationPaths() {
    }
}
