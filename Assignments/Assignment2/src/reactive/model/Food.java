package reactive.model;

/**
 * Food in the restaurant
 *
 * @param <T> The food that is produced from cooking it.
 */
public abstract class Food<T extends Food<T>> {
    protected final boolean isRaw;

    protected Food(boolean isRaw) {
        this.isRaw = isRaw;
    }

    /**
     * Cooks this food.
     *
     * @return The cooked food.
     */
    public abstract T cook();

    /**
     * Determines if this food is not raw.
     *
     * @return True if this food is not raw, false otherwise.
     */
    public boolean isNotRaw() {
        return !isRaw;
    }
}
