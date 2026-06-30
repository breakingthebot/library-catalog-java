/*
 * src/test/java/com/breakingthebot/librarycatalog/services/ApplicationVersionProviderTest.java
 * Verifies Maven metadata-backed application version lookup.
 * Connects to: pom.xml, src/main/java/com/breakingthebot/librarycatalog/services/ApplicationVersionProvider.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Tests application version lookup behavior.
 */
public final class ApplicationVersionProviderTest {
    /**
     * Verifies the provider returns a usable version string.
     */
    @Test
    void returnsVersionFromMetadataOrFallback() {
        ApplicationVersionProvider provider = new ApplicationVersionProvider();
        String version = provider.getVersion();

        assertTrue(!version.isBlank(), "Version should never be blank.");
        assertEquals("dev", version, "Classloader-based test runs should fall back to the development version.");
    }
}
