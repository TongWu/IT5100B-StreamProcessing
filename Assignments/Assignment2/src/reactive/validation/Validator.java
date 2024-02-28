package reactive.validation;

/**
 * A validator which allows test subjects to begin testing.
 *
 * @param <T> The type of the object to test.
 */
public class Validator<T> {
    private final T subject;

    private Validator(T t) {
        subject = t;
    }

    /**
     * Begins validating a test subject.
     *
     * @param t   The test subject
     * @param <T> The type of the test subject.
     * @return The resulting {@link Validator}.
     */
    public static <T> Validator<T> validate(T t) {
        return new Validator<>(t);
    }

    /**
     * Tests the current test subject with a {@link Test}.
     *
     * @param test The test to test the subject with.
     * @return The result of testing.
     */
    public Validation<T> with(Test<? super T> test) {
        return test.predicate.test(subject)
                ? new Validation<>(subject, ValidationResult.valid())
                : new Validation<>(subject, ValidationResult.invalid(test.reason));
    }

}
