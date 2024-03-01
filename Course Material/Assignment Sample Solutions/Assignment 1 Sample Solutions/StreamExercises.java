package streamexercises;

import streamexercises.utils.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.IntStream;
public class StreamExercises {
    /**
     * Computes the happy sum of n, being n^2 + (n - 1)^2 + ... + 1 + ... + n^2.
     * @param n n
     * @return The happy sum of n
     */
    public static int happySum(int n) {
        return IntStream.rangeClosed(1, n)
                .map(x -> x * x * 2)
                .sum() - 1;
    }

    /**
     * Creates a stream of sliding windows of a finite stream. This operation closes the input stream.
     * @param stream A finite stream.
     * @param windowSize The size of the window which must be positive.
     * @return A stream of sliding windows of the original stream.
     * @param <T> The type of the elements of the input stream.
     */
    public static <T> Stream<Stream<T>> window(Stream<T> stream, int windowSize) {
        if (windowSize <= 0) return Stream.empty();
        List<T> ls = stream.collect(Collectors.toList());
        return IntStream.range(0, ls.size() - windowSize + 1)
                .mapToObj(x -> IntStream.range(x, x + windowSize)
                        .mapToObj(ls::get));
    }

    /**
     * Creates a stream of the n-day moving average from a stream of daily temperatures.
     * This operation closes the input stream.
     * @param temperatures The stream of daily temperatures.
     * @param n The number of days of the moving average.
     * @return The stream of n-day moving averages.
     */
    public static DoubleStream movingAverage(DoubleStream temperatures, int n) {
        return window(temperatures.boxed(), n)
                .map(x -> x.mapToDouble(y -> y))
                .mapToDouble(x -> x.average().getAsDouble());
    }

    /**
     * Gets the total score of a bowling game that is represented as a stream of characters.
     * @param s A sequential stream of characters representing the bowling game.
     * @return The total score of the game.
     */
    public static int totalScore(Stream<Character> s) {
        return s.sequential()
                .reduce(new BowlingGameStatistics(), BowlingGameStatistics::put, (x, y) -> x)
                .get();
    }

    /**
     * Receives a stream of user state changes and collapses it into a query-able map of the latest state of each user.
     * @param userStateChanges The stream of user state changes.
     * @return The map of the latest states of the users.
     */
    public static Map<String, User> collectToDb(Stream<UserStateChange> userStateChanges) {
        return userStateChanges.parallel()
                .reduce(ImmutableMap.empty(), StreamExercises::changeStateOfUserInMap, StreamExercises::combineMaps)
                .toMap();
    }

    /**
     * Performs changes on a map of users using a {@link UserStateChange} event. If the target of the state change is not in the map, and empty {@link User} of the same ID will be created.
     * @param map The map of users.
     * @param u The state change event.
     * @return The resulting map with the updated user.
     */
    public static ImmutableMap<String, User> changeStateOfUserInMap(ImmutableMap<String, User> map, UserStateChange u) {
        String targetUserId = u.getTargetUserId();
        User current = map.getOrDefault(targetUserId, User.empty(targetUserId));
        User newUser = u.changeUserState(current);
        return map.put(targetUserId, newUser);
    }

    /**
     * Combines two maps to form a new map consisting of the most updated states of all users either of the two maps.
     * @param im1 The left map.
     * @param im2 The right map (considered to be more updated).
     * @return The resulting map consisting of the latest state.
     */
    public static ImmutableMap<String, User> combineMaps(ImmutableMap<String, User> im1, ImmutableMap<String, User> im2) {
        return im2.reduceEntries(im1, (x, y) -> {
            String userId = y.getKey();
            User rightUser = y.getValue();
            User leftUser = x.getOrDefault(userId, User.empty(userId));
            User newUser = leftUser.combineWith(rightUser);
            return x.put(userId, newUser);
        });
    }

    /**
     * Run this method to see {@link StreamExercises#collectToDb(Stream)} in action!
     * @param args Not used.
     */
    public static void main(String[] args) {
        Stream<UserStateChange> s = Stream.of(
                new UserNameChange("1", "Alice"),
                new UserNameChange("2", "Bob"),
                new UserNameChange("3", "Charlie"),
                new UserNameChange("4", "David"),
                new UserAccountBalanceIncrease("1", 1000),
                new UserAccountBalanceIncrease("2", 4500),
                new UserAccountBalanceIncrease("3", 400),
                new UserAccountBalanceIncrease("1", -500),
                new UserAccountBalanceIncrease("2",  500),
                new UserAccountBalanceIncrease("3", -200),
                new UserAccountBalanceIncrease("4", 200),
                new UserAccountBalanceIncrease("4", 500),
                new UserNameChange("1", "Alice Tan"),
                new UserAccountBalanceIncrease("3", 600)
        );
        System.out.println(collectToDb(s));
    }
}