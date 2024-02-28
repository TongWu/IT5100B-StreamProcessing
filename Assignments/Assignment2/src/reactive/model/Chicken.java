package reactive.model;

/**
 * A chicken in the restaurant.
 */
public class Chicken extends Food<Chicken> {

    /**
     * Creates a new raw chicken.
     */
    public Chicken() {
        super(true);
    }

    private Chicken(boolean isRaw) {
        super(isRaw);
    }

    /**
     * Cooks the chicken
     *
     * @return The resulting cooked chicken.
     */
    public Chicken cook() {
        return new Chicken(false);
    }

    @Override
    public String toString() {
        return isRaw ? "raw chicken" : "grilled chicken";
    }
}