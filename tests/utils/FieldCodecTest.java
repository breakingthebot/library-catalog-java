/*
 * tests/utils/FieldCodecTest.java
 * Verifies escaping and unescaping for persisted text fields.
 * Connects to: src/utils/FieldCodec.java, tests/utils/TestAssertions.java
 * Created: 2026-06-30
 */
package tests.utils;

import src.utils.FieldCodec;

/**
 * Tests field encoding and decoding behavior.
 */
public final class FieldCodecTest {
    private FieldCodecTest() {
    }

    /**
     * Runs all codec tests.
     */
    public static void runAll() {
        roundTripsControlCharacters();
        rejectsDanglingEscapeSequences();
    }

    /**
     * Verifies text survives a persistence round trip.
     */
    private static void roundTripsControlCharacters() {
        String value = "Title\twith tab\nand newline\\slash";
        String encoded = FieldCodec.encode(value);
        String decoded = FieldCodec.decode(encoded);

        TestAssertions.assertEquals(value, decoded, "Field codec should preserve supported text content.");
    }

    /**
     * Verifies invalid encodings fail loudly.
     */
    private static void rejectsDanglingEscapeSequences() {
        TestAssertions.assertThrows(
            () -> FieldCodec.decode("value\\"),
            IllegalArgumentException.class,
            "Field codec should reject dangling escapes."
        );
    }
}
