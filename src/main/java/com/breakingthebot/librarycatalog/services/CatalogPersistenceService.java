/*
 * src/services/CatalogPersistenceService.java
 * Saves and reloads the catalog state using a simple tab-delimited flat file.
 * Connects to: src/models/LibraryCatalogState.java, src/models/Book.java, src/models/Member.java, src/utils/FieldCodec.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.services;

import java.io.IOException;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.breakingthebot.librarycatalog.models.Book;
import com.breakingthebot.librarycatalog.models.LibraryCatalogState;
import com.breakingthebot.librarycatalog.models.Member;
import com.breakingthebot.librarycatalog.utils.FieldCodec;

/**
 * Persists the catalog to a plain-text format that is easy to inspect manually.
 */
public final class CatalogPersistenceService {
    private static final Logger LOGGER = Logger.getLogger(CatalogPersistenceService.class.getName());
    private static final String BOOK_PREFIX = "BOOK";
    private static final String MEMBER_PREFIX = "MEMBER";

    /**
     * Saves the catalog state to disk.
     *
     * @param state state snapshot to persist
     * @param path destination file path
     * @throws IOException when the file cannot be written
     */
    public void save(LibraryCatalogState state, Path path) throws IOException {
        validateState(state);
        validatePath(path);

        List<String> lines = new ArrayList<>();

        for (Book book : state.books()) {
            lines.add(String.join(
                "\t",
                BOOK_PREFIX,
                FieldCodec.encode(book.getId()),
                FieldCodec.encode(book.getTitle()),
                FieldCodec.encode(book.getAuthor()),
                Boolean.toString(book.isCheckedOut()),
                book.getDueDate().map(LocalDate::toString).orElse("")
            ));
        }

        for (Member member : state.members()) {
            lines.add(String.join(
                "\t",
                MEMBER_PREFIX,
                FieldCodec.encode(member.getId()),
                FieldCodec.encode(member.getName()),
                FieldCodec.encode(String.join(",", member.getBorrowedBookIds()))
            ));
        }

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(path, lines, StandardCharsets.UTF_8);
        LOGGER.log(Level.INFO, "Saved catalog snapshot to {0}", path.toAbsolutePath());
    }

    /**
     * Loads the catalog state from disk.
     *
     * @param path source file path
     * @return loaded state snapshot
     * @throws IOException when the file cannot be read
     */
    public LibraryCatalogState load(Path path) throws IOException {
        validatePath(path);

        if (!Files.exists(path)) {
            LOGGER.log(Level.WARNING, "Catalog file {0} does not exist. Returning empty state.", path.toAbsolutePath());
            return new LibraryCatalogState(List.of(), List.of());
        }

        List<Book> books = new ArrayList<>();
        List<Member> members = new ArrayList<>();

        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }

            parseLine(line, books, members);
        }

        LOGGER.log(Level.INFO, "Loaded catalog snapshot from {0}", path.toAbsolutePath());
        return new LibraryCatalogState(books, members);
    }

    /**
     * Indicates whether the catalog file currently exists.
     *
     * @param path catalog file path
     * @return true when the path exists
     */
    public boolean exists(Path path) {
        validatePath(path);
        return Files.exists(path);
    }

    /**
     * Parses one persisted line into a book or member record.
     *
     * @param line persisted line
     * @param books book accumulator
     * @param members member accumulator
     */
    private void parseLine(String line, List<Book> books, List<Member> members) {
        String[] fields = line.split("\t", -1);

        if (fields.length == 0) {
            throw new IllegalArgumentException("Invalid catalog line: no fields present.");
        }

        String recordType = fields[0];

        if (BOOK_PREFIX.equals(recordType)) {
            books.add(parseBook(fields));
            return;
        }

        if (MEMBER_PREFIX.equals(recordType)) {
            members.add(parseMember(fields));
            return;
        }

        throw new IllegalArgumentException("Invalid catalog line: unknown record type " + recordType);
    }

    /**
     * Parses one book record.
     *
     * @param fields persisted fields
     * @return parsed book
     */
    private Book parseBook(String[] fields) {
        if (fields.length != 5 && fields.length != 6) {
            throw new IllegalArgumentException("Invalid book record: expected 5 or 6 fields.");
        }

        LocalDate dueDate = null;
        if (fields.length == 6 && !fields[5].isBlank()) {
            dueDate = LocalDate.parse(fields[5]);
        }

        return new Book(
            FieldCodec.decode(fields[1]),
            FieldCodec.decode(fields[2]),
            FieldCodec.decode(fields[3]),
            Boolean.parseBoolean(fields[4]),
            dueDate
        );
    }

    /**
     * Parses one member record.
     *
     * @param fields persisted fields
     * @return parsed member
     */
    private Member parseMember(String[] fields) {
        if (fields.length != 4) {
            throw new IllegalArgumentException("Invalid member record: expected 4 fields.");
        }

        String borrowedIdsField = FieldCodec.decode(fields[3]);
        Set<String> borrowedIds = new LinkedHashSet<>();

        if (!borrowedIdsField.isBlank()) {
            for (String borrowedId : borrowedIdsField.split(",")) {
                borrowedIds.add(borrowedId);
            }
        }

        return new Member(FieldCodec.decode(fields[1]), FieldCodec.decode(fields[2]), borrowedIds);
    }

    /**
     * Rejects invalid state inputs.
     *
     * @param state state snapshot
     */
    private void validateState(LibraryCatalogState state) {
        if (state == null) {
            throw new IllegalArgumentException("Catalog state is required.");
        }
    }

    /**
     * Rejects invalid paths.
     *
     * @param path filesystem path
     */
    private void validatePath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path is required.");
        }
    }
}

