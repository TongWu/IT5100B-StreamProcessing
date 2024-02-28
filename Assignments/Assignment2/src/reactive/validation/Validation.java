package reactive.validation;

/**
 * The result of validating an object with a {@link Test}. The test subject can be subject to further tests.
 *
 * @param <T> The type of the object to test.
 */
public class Validation<T> {
    private final ValidationResult res;
    private final T subject;

    /**
     * @param t   The test subject.
     * @param res The test result.
     */
    Validation(T t, ValidationResult res) {
        subject = t;
        this.res = res;
    }

    /**
     * Tests the subject using another {@link Test}.
     *
     * @param test The other {@link Test}.
     * @return The result of also testing the test subject using another {@link Test}.
     */
    public Validation<T> and(Test<? super T> test) {
        Validation<T> that = Validator.validate(subject).with(test);
        return new Validation<>(subject, this.res.and(that.res));
    }

    /**
     * The conjunction of this {@link ValidationResult} and another {@link ValidationResult}.
     *
     * @param that The other {@link Validation}.
     * @return The conjunction of both {@link ValidationResult}s.
     */
    public ValidationResult and(Validation<?> that) {
        return this.res.and(that.res);
    }

    /**
     * Determines if the test resulted in validity.
     *
     * @return True if the test subject was successfully validated.
     */
    public boolean isValid() {
        return res.isValid();
    }

    /**
     * Get the reason of test failure ({@code null} if the test has passed).
     *
     * @return The reason for test failure.
     */
    public String getReason() {
        return res.getReason();
    }
}
