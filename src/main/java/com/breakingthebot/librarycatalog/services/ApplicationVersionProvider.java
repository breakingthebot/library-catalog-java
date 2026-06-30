/*
 * src/main/java/com/breakingthebot/librarycatalog/services/ApplicationVersionProvider.java
 * Reads the packaged application version from Maven-generated classpath metadata.
 * Connects to: pom.xml, src/main/java/com/breakingthebot/librarycatalog/services/LibraryCatalogApplicationRunner.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resolves the application version from Maven metadata on the classpath.
 */
public final class ApplicationVersionProvider {
    private static final String VERSION_RESOURCE = "/META-INF/maven/com.breakingthebot/library-catalog-java/pom.properties";
    private static final String FALLBACK_VERSION = "dev";

    /**
     * Returns the current application version.
     *
     * @return resolved application version
     */
    public String getVersion() {
        try (InputStream inputStream = ApplicationVersionProvider.class.getResourceAsStream(VERSION_RESOURCE)) {
            if (inputStream == null) {
                return FALLBACK_VERSION;
            }

            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("version", FALLBACK_VERSION).trim();
        } catch (IOException exception) {
            return FALLBACK_VERSION;
        }
    }
}
