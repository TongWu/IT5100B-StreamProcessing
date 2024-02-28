package reactive.validation;

import java.util.function.Predicate;

/**
 * A test.
 *
 * @param <T> The type of the object to test.
 */
public class Test<T> {

    /**
     * The predicate to test the object with.
     */
    public final Predicate<T> predicate;

    /**
     * The reason for failing the predicate.
     */
    public final String reason;

    private Test(Predicate<T> predicate, String reason) {
        this.predicate = predicate;
        this.reason = reason;
    }

    /**
     * Creates a test.
     *
     * @param predicate The predicate used to test the object.
     * @param reason    The reason an object would fail this test.
     * @param <T>       The type of the object to test.
     * @return The test.
     */
    public static <T> Test<T> of(Predicate<T> predicate, String reason) {
        return new Test<>(predicate, reason);
    }
}
