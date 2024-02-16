package historical;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The {@code Historical<T>} class is a wrapper class that keeps track of state changes to an object. {@code Historical}
 * objects are <strong>immutable</strong>.
 * @param <T> The type of the current value.
 */
public class Historical<T> {
    // The current value as an Optional (the value may be null)
    private final Optional<T> value;

    // The previous value as an Optional (there may be no previous value). The type of the previous value is lost,
    // hence Historical<?>.
    private final Optional<Historical<?>> previous;

    // A simple private constructor
    private Historical(Optional<T> value, Optional<Historical<?>> previous) {
        this.value = value;
        this.previous = previous;
    }

    // A simple implementation of toString
    public String toString() {
        return previous.map(x -> x + " -> ")
                .orElse("")
                + getStringFromOptional(this.value);
    }

    // Simpler Optional string representation
    private static String getStringFromOptional(Optional<?> opt) {
        return opt.map(Object::toString)
                .orElse("null");
    }

    // Compare-by-value implementation
    @Override
    public boolean equals(Object o) {
        return (o instanceof Historical)
                && ((Historical<?>) o).value.equals(this.value)
                && ((Historical<?>) o).previous.equals(this.previous);
    }

    /**
     * Constructs a {@code Historical} object with an initial value and an empty history.
     * @param value The initial value.
     * @return The {@code Historical} object containing that initial value.
     * @param <T> The type of the initial value.
     */
    public static <T> Historical<T> of(T value) {
        // TODO: Complete the of() method
        // throw new RuntimeException("Historical::of has not been implemented yet!");
        return new Historical<>(Optional.ofNullable(value), Optional.empty());
    }

    /**
     * Gets the current value, which may not be present.
     * @return The current value.
     */
    public Optional<T> get() {
        // TODO: Complete the get() method
        // throw new RuntimeException("Historical::get has not been implemented yet!");
        return this.value;
    }

    /**
     * Returns a new {@code Historical} object with the current value replaced with a new value. The previous value(s)
     * are all still retained in the new {@code Historical} object.
     * @param newValue The new value to replace the current value with.
     * @return The resulting {@code Historical} object where the current value has been replaced.
     * @param <R> The type of the new value.
     */
    public <R> Historical<R> replace(R newValue) {
        // TODO: Complete the replace(R) method
        // throw new RuntimeException("Historical::replace has not been implemented yet!");

        // If new value equals to the current value
        if (this.value.equals(Optional.ofNullable(newValue))) {
            // Return current object
            return (Historical<R>) this;
        } else {
            // Else, create a new object, with historical value
            return new Historical<>(Optional.ofNullable(newValue), Optional.of(this));
        }
    }

    /**
     * Maps the current value using a function. If the current value is present, then the map operation proceeds as per
     * normal; otherwise, the current value remains null. <strong>This operation does
     * not map the previous values</strong>.
     * @param f The mapping function.
     * @return The resulting {@code Historical} object containing the mapped value (and its history).
     * @param <R> The type of the new object after applying the function on the current value.
     */
    public <R> Historical<R> map(Function<? super T, ? extends R> f) {
        // TODO: Complete the map(Function<? super T, ? extends R>) method
        // throw new RuntimeException("Historical::map has not been implemented yet!");

        return this.value.map(v -> replace(f.apply(v))).orElse((Historical<R>) this);
    }

    /**
     * Filters the current value based on a {@code Predicate}. If the current value passes the predicate, then the
     * current value remains. Otherwise, the current value becomes null. <strong>This operation does not
     * filter the previous values</strong>.
     * @param f The predicate.
     * @return The resulting {@code Historical} object after filtering the current value.
     */
    public Historical<T> filter(Predicate<? super T> f) {
        // TODO: Complete the filter(Predicate<? super T>) method
        // throw new RuntimeException("Historical::filter has not been implemented yet!");

        return this.value.filter(f).map(v -> this).orElseGet(() -> replace(null));
    }

    /**
     * Maps the current element (if present) into a new {@code Historical} object {@code x}. The result is the {@code Historical}
     * object whose current value is the current value {@code x}, and whose history is the
     * concatenation of histories of this object and {@code x}.
     * @param f The mapping function.
     * @return The resulting {@code Historical} object from the {@code flatMap} operation.
     * @param <R> The type of the value of the resulting {@code Historical} object.
     */
    public <R> Historical<R> flatMap(Function<? super T, ? extends Historical<R>> f) {
        // TODO: Complete the flatMap(Function<? super T, ? extends Historical<R>>) method
        // throw new RuntimeException("Historical::flatMap has not been implemented yet!");

        return this.value.map(v -> {
            // Apply the mapping function to the current value.
            Historical<R> mappedResult = f.apply(v);
            // Add a pointer for the current object
            Historical<R> current = (Historical<R>) this;

            // Concat with the previous history objects elements
            return concat(current, mappedResult);
        }).orElse((Historical<R>) this);

    }

    private <R> Historical<R> concat(Historical<R> base, Historical<R> target) {
        // If there is a previous attribute, call concat recursively
        return target
                .previous
                .map(prev -> concat(base, (Historical<R>) prev)
                .replace(target.value.get()))
                .orElse(base);
    }

    /**
     * Produces the previous {@code Historical} object that led up to this object, if it exists.
     * @return The previous {@code Historical} object.
     */
    public Optional<Historical<?>> undo() {
        // TODO: Complete the undo() method
        // throw new RuntimeException("Historical::undo has not been implemented yet!");
        return this.previous;
    }

}
