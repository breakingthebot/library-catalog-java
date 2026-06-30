/*
 * src/main/java/models/ApplicationExecutionResult.java
 * Carries CLI output, error output, and exit code for one application run.
 * Connects to: src/main/java/LibraryCatalogApplication.java, src/main/java/services/LibraryCatalogApplicationRunner.java
 * Created: 2026-06-30
 */
package src.models;

/**
 * Immutable result for one application execution.
 *
 * @param output user-facing standard output text
 * @param errorOutput user-facing error output text
 * @param exitCode process exit code
 */
public record ApplicationExecutionResult(String output, String errorOutput, int exitCode) {
    /**
     * Validates the execution result fields.
     *
     * @param output user-facing standard output text
     * @param errorOutput user-facing error output text
     * @param exitCode process exit code
     */
    public ApplicationExecutionResult {
        output = output == null ? "" : output;
        errorOutput = errorOutput == null ? "" : errorOutput;
    }

    /**
     * Creates a successful result.
     *
     * @param output standard output text
     * @return successful execution result
     */
    public static ApplicationExecutionResult success(String output) {
        return new ApplicationExecutionResult(output, "", 0);
    }

    /**
     * Creates a failed result.
     *
     * @param errorOutput standard error text
     * @param exitCode failure exit code
     * @return failed execution result
     */
    public static ApplicationExecutionResult failure(String errorOutput, int exitCode) {
        return new ApplicationExecutionResult("", errorOutput, exitCode);
    }

    /**
     * Indicates whether the execution succeeded.
     *
     * @return true when the exit code is zero
     */
    public boolean isSuccess() {
        return exitCode == 0;
    }
}
