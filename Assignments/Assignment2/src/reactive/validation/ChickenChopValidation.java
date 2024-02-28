package reactive.validation;

import reactive.model.Chicken;
import reactive.model.Food;
import reactive.model.Fries;

/**
 * Tests for whether the ingredients for a chicken chop can be assembled into a chicken chop.
 */
public interface ChickenChopValidation {
    /**
     * A test to see if the chicken is not raw.
     */
    Test<Chicken> CHICKEN_IS_NOT_RAW = Test.of(Food::isNotRaw, "chicken is raw");
    /**
     * A test to see if the fries is not raw.
     */
    Test<Fries> FRIES_IS_NOT_RAW = Test.of(Food::isNotRaw, "fries is raw");
}
