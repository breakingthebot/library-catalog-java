/*
 * src/utils/FieldCodec.java
 * Escapes and unescapes text fields used by the flat-file persistence format.
 * Connects to: src/services/CatalogPersistenceService.java
 * Created: 2026-06-30
 */
package com.breakingthebot.librarycatalog.utils;

/**
 * Encodes fields so tab-delimited persistence remains safe for user-entered text.
 */
public final class FieldCodec {
    private FieldCodec() {
    }

    /**
     * Escapes supported control characters.
     *
     * @param value raw text field
     * @return encoded text field
     */
    public static String encode(String value) {
        validateInput(value);

        return value
            .replace("\\", "\\\\")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    /**
     * Reverses the supported escape sequences.
     *
     * @param value encoded text field
     * @return decoded text field
     */
    public static String decode(String value) {
        validateInput(value);

        StringBuilder builder = new StringBuilder();
        boolean escaping = false;

        for (int index = 0; index < value.length(); index++) {
            char current = value.charAt(index);

            if (!escaping) {
                if (current == '\\') {
                    escaping = true;
                } else {
                    builder.append(current);
                }

                continue;
            }

            builder.append(resolveEscape(current));
            escaping = false;
        }

        if (escaping) {
            throw new IllegalArgumentException("Invalid encoded field: dangling escape sequence.");
        }

        return builder.toString();
    }

    /**
     * Resolves a single escape sequence.
     *
     * @param marker escape marker
     * @return decoded character
     */
    private static char resolveEscape(char marker) {
        return switch (marker) {
            case '\\' -> '\\';
            case 't' -> '\t';
            case 'n' -> '\n';
            case 'r' -> '\r';
            default -> throw new IllegalArgumentException("Invalid encoded field: unsupported escape '\\" + marker + "'.");
        };
    }

    /**
     * Rejects null fields so callers fail loudly.
     *
     * @param value field to validate
     */
    private static void validateInput(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid field: value is required.");
        }
    }
}

