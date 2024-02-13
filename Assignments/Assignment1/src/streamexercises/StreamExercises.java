package streamexercises;

import streamexercises.utils.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamExercises {
    /**
     * Computes the happy sum of n, being n^2 + (n - 1)^2 + ... + 1 + ... + n^2.
     * @param n n
     * @return The happy sum of n
     */
    public static int happySum(int n) {
        // TODO: Complete the happySum(int) method
        // throw new RuntimeException("StreamExercises::happySum has not been implemented yet!");
        
        return IntStream.rangeClosed(1, n)
                        .map(i -> i*i)      // Sequence from 1^2 to n^2
                        .sum()
                + IntStream.range(0, n-1)      // Sequence from n^2 to 2^2
                        .map(i -> (n-i)*(n-i))
                        .sum();
    }
    
    /**
     * Creates a stream of sliding windows of a finite stream. This operation closes the input stream.
     * @param stream A finite stream.
     * @param windowSize The size of the window which must be positive.
     * @return A stream of sliding windows of the original stream.
     * @param <T> The type of the elements of the input stream.
     */
    public static <T> Stream<Stream<T>> window(Stream<T> stream, int windowSize) {
        // TODO: Complete the window method
        // throw new RuntimeException("StreamExercises::window has not been implemented yet!");
        
        // If window size is not positive, return an empty stream
        if (windowSize <= 0) {
            return Stream.empty();
        }
        
        // Convert a stream to list
        List<T> list = stream.collect(Collectors.toList());
        // Create a sliding window stream
        return IntStream.range(0, list.size()-windowSize+1)
                .mapToObj(start -> list.subList(start, start+windowSize).stream())
                .collect(Collectors.toList()).stream();
    }

    /**
     * Creates a stream of the n-day moving average from a stream of daily temperatures.
     * This operation closes the input stream.
     * @param temperatures The stream of daily temperatures.
     * @param n The number of days of the moving average.
     * @return The stream of n-day moving averages.
     */
    public static DoubleStream movingAverage(DoubleStream temperatures, int n) {
        // TODO: Complete the movingAverage method
        // throw new RuntimeException("StreamExercises::movingAverage has not been implemented yet!");

        // Convert double stream to stream<double>
        Stream<Double> temp = temperatures.boxed();
        // Create windows for temperatures lst
        Stream<Stream<Double>> windowTemp = window(temp, n);
        // Calculate each window's average
        Stream<Double> averageTemp = windowTemp.map(window -> window.collect(Collectors.averagingDouble(Double::doubleValue)));

        return averageTemp.mapToDouble(Double::doubleValue);
    }

    /**
     * Gets the total score of a bowling game that is represented as a stream of characters.
     * @param s A sequential stream of characters representing the bowling game.
     * @return The total score of the game.
     */
    public static int totalScore(Stream<Character> s) {
        // TODO: Complete the totalScore method
        // throw new RuntimeException("StreamExercises::totalScore has not been implemented yet!");
        BowlingGameStatistics score = s.reduce(
                new BowlingGameStatistics(),        // Initial score
                (game, result) -> game.put(result), // Sum score
                (game1, game2) -> game1             // Concat function
        );

        return score.get();
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
        // TODO: Complete the changeStateOfUserInMap method
        // throw new RuntimeException("StreamExercises::changeStateOfUserInMap has not been implemented yet!");

        // Get user object according to the user ID, if not exist then create an empty user
        User user = map.getOrDefault(u.getTargetUserId(), User.empty(u.getTargetUserId()));
        // Then update the user state
        User updated = u.changeUserState(user);
        return map.put(u.getTargetUserId(), updated);
    }

    /**
     * Combines two maps to form a new map consisting of the most updated states of all users either of the two maps.
     * @param im1 The left map.
     * @param im2 The right map (considered to be more updated).
     * @return The resulting map consisting of the latest state.
     */
    public static ImmutableMap<String, User> combineMaps(ImmutableMap<String, User> im1, ImmutableMap<String, User> im2) {
        // TODO: Complete the combineMaps method
        // throw new RuntimeException("StreamExercises::combineMaps has not been implemented yet!");
        return im2.reduceEntries(im1, (accMap, entry) -> {
            User existingUser = accMap.getOrDefault(entry.getKey(), User.empty(entry.getKey()));    // Check status
            User newUser = entry.getValue();                                                        // Get user status
            User combinedUser = existingUser.combineWith(newUser);                                  // Combine user status between im1 and im2
            return accMap.put(entry.getKey(), combinedUser);                                        // Update accMap
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