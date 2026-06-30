/*
 * tests/utils/TestAssertions.java
 * Provides minimal assertions for the custom Java test runner.
 * Connects to: project test classes under tests/
 * Created: 2026-06-30
 */
package tests.utils;

/**
 * Lightweight assertions so tests can run without third-party dependencies.
 */
public final class TestAssertions {
    private TestAssertions() {
    }

    /**
     * Asserts that two values are equal.
     *
     * @param expected expected value
     * @param actual actual value
     * @param message failure message
     */
    public static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
        }
    }

    /**
     * Asserts that a condition is true.
     *
     * @param condition condition to verify
     * @param message failure message
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts that a condition is false.
     *
     * @param condition condition to verify
     * @param message failure message
     */
    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Asserts that running the action throws the expected exception type.
     *
     * @param action action under test
     * @param expectedType expected exception type
     * @param message failure message
     */
    public static void assertThrows(Runnable action, Class<? extends Throwable> expectedType, String message) {
        try {
            action.run();
        } catch (Throwable throwable) {
            if (expectedType.isInstance(throwable)) {
                return;
            }

            throw new AssertionError(message + " Expected exception: " + expectedType.getName() + ", Actual: " + throwable);
        }

        throw new AssertionError(message + " Expected exception: " + expectedType.getName());
    }
}
