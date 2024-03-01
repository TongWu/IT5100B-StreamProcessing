package reactive;
import reactive.model.Chicken;
import reactive.model.ChickenChop;
import reactive.model.Food;
import reactive.model.Fries;
import reactive.service.FoodService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactiveExercises {

    // TODO: Complete the happySum method (change the type signature too).
    /**
     * Obtains the happy sum of {@code n}.
     *
     * @param n A positive integer.
     * @return The happy sum of {@code n}.
     */
    public static Mono<Integer> happySum(int n) {
        return Flux.range(1, n)
                .map(x -> x*x) // 1+4+...+x^2
                .reduce(0, Integer::sum)
                .map(sum -> sum*2-1); // x^2+...+4+1+4+...+x^2
    }

    // TODO: Complete the movingAverages method (change the type signature too).
    /**
     * Obtains the {@code n}-day moving average temperatures of a stream of temperatures recorded from IoT sensors.
     *
     * @param temperatures The stream of temperatures.
     * @param n            The number of days in the sliding window.
     * @return The stream of {@code n}-day moving average temperatures.
     */
    public static Flux<Double> movingAverages(Flux<Double> temperatures, int n) {
        return temperatures.buffer(n,1)
                .filter(list -> list.size() == n)
                .map(list->list.stream().mapToDouble(d->d).average().orElse(Double.NaN));
    }

    // TODO: Complete the chickenChop method (change the type signature too).
    /**
     * Obtains a chicken chop reactively.
     *
     * @return A chicken chop.
     */
    public static Mono<ChickenChop> chickenChop() {
        // 1. Get a chicken and grill it
        // FlatMap is used to get raw chicken then grill it
        Mono<Chicken> chicken = FoodService.getRawChicken().flatMap(FoodService::grillChicken);
        // 2. Get a fries and fry it
        Mono<Fries> fries = FoodService.getRawFries().flatMap(FoodService::fryFries);
        return Mono
                .zip(chicken, fries)
                .flatMap(assembled -> FoodService.assemble(assembled.getT1(), assembled.getT2()));
    }

    public static void main(String[] args) {
        Mono<ChickenChop> test = chickenChop();
        test.block();
        test.subscribe(System.out::println);
    }

}
