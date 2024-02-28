package reactive.model;

/**
 * Some fries in our restaurant.
 */
public class Fries extends Food<Fries> {

    /**
     * Creates some raw fries.
     */
    public Fries() {
        super(true);
    }

    private Fries(boolean isRaw) {
        super(isRaw);
    }

    /**
     * Cooks this fries.
     *
     * @return Cooked fries.
     */
    public Fries cook() {
        return new Fries(false);
    }

    @Override
    public String toString() {
        return isRaw ? "raw fries" : "cooked fries";
    }
}