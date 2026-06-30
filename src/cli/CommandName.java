/*
 * src/cli/CommandName.java
 * Enumerates the supported command-line actions for the library catalog.
 * Connects to: src/cli/CommandLineParser.java, src/models/CommandRequest.java, src/services/LibraryCatalogCliService.java
 * Created: 2026-06-30
 */
package src.cli;

/**
 * Supported command names for the catalog CLI.
 */
public enum CommandName {
    HELP,
    SEED,
    ADD_BOOK,
    ADD_MEMBER,
    CHECKOUT,
    RETURN,
    LIST_BOOKS,
    LIST_MEMBERS
}
