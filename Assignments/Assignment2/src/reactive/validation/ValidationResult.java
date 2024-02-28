package reactive.validation;

/**
 * The result of testing.
 */
public class ValidationResult {
    private final boolean valid;
    private final String reason;

    private ValidationResult(boolean valid, String reason) {
        this.valid = valid;
        this.reason = reason;
    }

    /**
     * A valid result.
     *
     * @return A valid result.
     */
    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    /**
     * An invalid result.
     *
     * @param reason The reason for failing a test.
     * @return The invalid result.
     */
    public static ValidationResult invalid(String reason) {
        return new ValidationResult(false, reason);
    }

    /**
     * Gets the result of the test.
     *
     * @return The result of the test.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the reason for failing a test ({@code null} if the test has passed).
     *
     * @return The reason for failing a test.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Performs a conjunction on this result and another {@link ValidationResult}.
     *
     * @param that The other {@link ValidationResult}.
     * @return The resulting {@link ValidationResult}.
     */
    public ValidationResult and(ValidationResult that) {
        boolean res = this.valid && that.valid;
        if (res) return ValidationResult.valid();
        if (this.valid) return that;
        if (that.valid) return this;
        return ValidationResult.invalid(this.reason + " and " + that.reason);
    }
}
