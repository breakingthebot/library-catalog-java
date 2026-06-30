/*
 * src/test/java/utils/FieldCodecTest.java
 * Verifies escaping and unescaping for persisted text fields with JUnit 5.
 * Connects to: src/main/java/utils/FieldCodec.java
 * Created: 2026-06-30
 */
package tests.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import src.utils.FieldCodec;

/**
 * Tests field encoding and decoding behavior.
 */
public final class FieldCodecTest {
    /**
     * Verifies text survives a persistence round trip.
     */
    @Test
    void roundTripsControlCharacters() {
        String value = "Title\twith tab\nand newline\\slash";
        String encoded = FieldCodec.encode(value);
        String decoded = FieldCodec.decode(encoded);

        assertEquals(value, decoded, "Field codec should preserve supported text content.");
    }

    /**
     * Verifies invalid encodings fail loudly.
     */
    @Test
    void rejectsDanglingEscapeSequences() {
        assertThrows(
            IllegalArgumentException.class,
            () -> FieldCodec.decode("value\\"),
            "Field codec should reject dangling escapes."
        );
    }
}
