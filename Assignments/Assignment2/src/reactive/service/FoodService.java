package reactive.service;

import reactive.model.Chicken;
import reactive.model.ChickenChop;
import reactive.model.Food;
import reactive.model.Fries;
import reactive.validation.Validation;
import reactive.validation.ValidationResult;
import reactive.validation.Validator;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.function.Supplier;

import static reactive.validation.ChickenChopValidation.CHICKEN_IS_NOT_RAW;
import static reactive.validation.ChickenChopValidation.FRIES_IS_NOT_RAW;

/**
 * A service class for dealing with food.
 */
public class FoodService {
    private static final Random RNG = new Random();

    /**
     * Gets a raw chicken
     *
     * @return A raw chicken
     */
    public static Mono<Chicken> getRawChicken() {
        return getRawFood(Chicken::new);
    }

    /**
     * Grills a chicken
     *
     * @param c a raw chicken
     * @return A grilled chicken
     */
    public static Mono<Chicken> grillChicken(Chicken c) {
        return cookFood(c);
    }

    /**
     * Gets some raw fries
     *
     * @return Some raw fries
     */
    public static Mono<Fries> getRawFries() {
        return getRawFood(Fries::new);
    }

    /**
     * Fries some raw fries
     *
     * @param f Some raw fries
     * @return Cooked fries
     */
    public static Mono<Fries> fryFries(Fries f) {
        return cookFood(f);
    }

    /**
     * Assembles cooked chicken and fries
     *
     * @param c the grilled chicken
     * @param f the cooked fries
     * @return the resulting chicken chop
     */
    public static Mono<ChickenChop> assemble(Chicken c, Fries f) {
        Validation<Chicken> chickenIsValid = Validator.validate(c).with(CHICKEN_IS_NOT_RAW);
        Validation<Fries> friesIsValid = Validator.validate(f).with(FRIES_IS_NOT_RAW);
        ValidationResult res = chickenIsValid.and(friesIsValid);
        if (!res.isValid()) throw new IllegalArgumentException(res.getReason());
        int delay = RNG.nextInt(2000);
        return Mono.just(c)
                .zipWith(Mono.just(f), ChickenChop::new)
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(x -> System.out.printf("got %s after %.1fs\n", x, delay / 1000.0));
    }

    private static <T extends Food<T>> Mono<T> getRawFood(Supplier<T> f) {
        int delay = RNG.nextInt(2000);
        return Mono.just(f.get())
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(x -> System.out.printf("got %s after %.1fs\n", x, delay / 1000.0));
    }

    private static <T extends Food<T>> Mono<T> cookFood(Food<T> f) {
        int delay = RNG.nextInt(2000);
        return Mono.just(f.cook())
                .delayElement(Duration.ofMillis(delay))
                .doOnNext(x -> System.out.printf("got %s after %.1fs\n", x, delay / 1000.0));
    }
}
