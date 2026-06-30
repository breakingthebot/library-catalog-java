/*
 * src/models/LibraryCatalogState.java
 * Holds a full snapshot of books and members for persistence and reloads.
 * Connects to: src/services/LibraryCatalogService.java, src/services/CatalogPersistenceService.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.models;

import java.util.List;

/**
 * Immutable state snapshot for the catalog.
 *
 * @param books catalog books
 * @param members library members
 */
public record LibraryCatalogState(List<Book> books, List<Member> members) {
    /**
     * Creates an immutable snapshot with defensive copies.
     *
     * @param books catalog books
     * @param members library members
     */
    public LibraryCatalogState {
        books = List.copyOf(books);
        members = List.copyOf(members);
    }
}

