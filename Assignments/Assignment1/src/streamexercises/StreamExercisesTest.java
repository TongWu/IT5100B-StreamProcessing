package streamexercises;

import streamexercises.utils.ImmutableMap;
import streamexercises.utils.User;
import streamexercises.utils.UserAccountBalanceIncrease;
import streamexercises.utils.UserNameChange;
import testingutils.UncomputedTestSet;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static testingutils.Tester.testAll;

public class StreamExercisesTest {

    public static void main(String[] args) {
        testAll(List.of(
                StreamExercisesTest::public_test1,
                StreamExercisesTest::public_test2,
                StreamExercisesTest::public_test3,
                StreamExercisesTest::public_test4,
                StreamExercisesTest::public_test5,
                StreamExercisesTest::public_test6,
                StreamExercisesTest::public_test7,
                StreamExercisesTest::public_test8,
                StreamExercisesTest::public_test9
        ));
    }

    private static <T> List<List<T>> streamsToLists(Stream<Stream<T>> stream) {
        return stream.map(x -> x.collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static UncomputedTestSet public_test1() {
        return new UncomputedTestSet(
                "happySum() Test",
                "happySum(5) = 109",
                "> happySum(5)",
                () -> 109,
                () -> StreamExercises.happySum(5),
                1);
    }

    private static UncomputedTestSet public_test2() {
        return new UncomputedTestSet(
                "window Test 1",
                "test case shown in question",
                "> window(Stream.of(1, 2, 3, 4, 5), 3)",
                () -> List.of(List.of(1, 2, 3), List.of(2, 3, 4), List.of(3, 4, 5)),
                () -> streamsToLists(StreamExercises.window(Stream.of(1, 2, 3, 4, 5), 3)),
                0.5);
    }

    private static UncomputedTestSet public_test3() {
        return new UncomputedTestSet(
                "window Test 2",
                "an invalid window size should produce an empty stream",
                "> window(Stream.of(1, 2, 3, 4, 5), 0)",
                () -> List.of(),
                () -> streamsToLists(StreamExercises.window(Stream.of(1, 2, 3, 4, 5), 0)),
                0.5);
    }

    private static UncomputedTestSet public_test4() {
        return new UncomputedTestSet(
                "movingAverage Test",
                "straightforward test",
                "> movingAverage(DoubleStream.of(1, 2, 3, 4, 5), 3)",
                () -> List.<Double>of(2.0, 3.0, 4.0),
                () -> StreamExercises.movingAverage(DoubleStream.of(1, 2, 3, 4, 5), 3).boxed().collect(Collectors.toList()),
                1
        );
    }
    private static UncomputedTestSet public_test5() {
        return new UncomputedTestSet(
                "totalScore Test",
                "All strikes should give 300 points",
                "> totalScore(Stream.of('X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X'))",
                () -> 300,
                () -> StreamExercises.totalScore(Stream.of('X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X')),
                1
        );
    }

    private static UncomputedTestSet public_test6() {
        return new UncomputedTestSet(
                "totalScore Test",
                "A more complicated test case",
                "> totalScore(Stream.of('5', '4', 'X', '0', '/', '4', '3', '7', '1', 'X', '0', '0', '2', '1', '3', '3', 'X'))",
                () -> 143,
                () -> StreamExercises.totalScore(Stream.of('5', '4', 'X', '0', '/', '4', '3', '7', '1', 'X', '0', '0', '2', '1', '3', '3', 'X')),
                1
        );
    }

    private static UncomputedTestSet public_test7() {
        return new UncomputedTestSet(
                "changeStateOfUserInMap Test 1",
                "changeStateOfUserInMap should process a state change on the user in the map",
                "> User alice = User.empty(\"123\").ofAccountBalance(4000).ofName(\"Alice\");\n" +
                        "> ImmutableMap<String, User> im1 = ImmutableMap.<String, User>empty().put(\"123\", alice);\n" +
                        "> changeStateOfUserInMap(im1, new UserAccountBalanceIncrease(\"123\", 300));",
                () -> ImmutableMap.<String, User>empty().put("123", User.empty("123").ofAccountBalance(4300).ofName("Alice")),
                () -> {
                    User alice = User.empty("123").ofAccountBalance(4000).ofName("Alice");
                    ImmutableMap<String, User> im1 = ImmutableMap.<String, User>empty().put("123", alice);
                    return StreamExercises.changeStateOfUserInMap(im1, new UserAccountBalanceIncrease("123", 300));
                },
                0.5
        );
    }

    private static UncomputedTestSet public_test8() {
        return new UncomputedTestSet(
                "changeStateOfUserInMap Test 2",
                "changeStateOfUserInMap should process a state change on a user that doesn't exist in the map",
                "> User alice = User.empty(\"123\").ofAccountBalance(4000).ofName(\"Alice\");\n" +
                        "> ImmutableMap<String, User> im1 = ImmutableMap.<String, User>empty().put(\"123\", alice);\n" +
                        "> changeStateOfUserInMap(im1, new UserAccountBalanceIncrease(\"123\", 300));",
                () -> ImmutableMap.<String, User>empty().put("123", User.empty("123").ofAccountBalance(300).ofName("Alice")),
                () -> StreamExercises.changeStateOfUserInMap(StreamExercises.changeStateOfUserInMap(ImmutableMap.empty(), new UserNameChange("123", "Alice")), new UserAccountBalanceIncrease("123", 300)),
                0.5
        );
    }

    private static UncomputedTestSet public_test9() {
        return new UncomputedTestSet(
                "combineMaps Test",
                "combineMaps should combine two maps to reflect the updated states of all users",
                "> ImmutableMap<String, User> im1 = ImmutableMap.<String, User>empty()\n" +
                        "        .put(\"1\", User.empty(\"1\")\n" +
                        "                .ofName(\"Bob\")\n" +
                        "                .ofAccountBalance(1000))\n" +
                        "        .put(\"2\", User.empty(\"2\")\n" +
                        "                .ofAccountBalance(-100));\n" +
                        "> ImmutableMap<String, User> im2 = ImmutableMap.<String, User>empty()\n" +
                        "        .put(\"1\", User.empty(\"1\")\n" +
                        "            .ofAccountBalance(-200))\n" +
                        "        .put(\"2\", User.empty(\"2\")\n" +
                        "            .ofAccountBalance(1000)\n" +
                        "            .ofName(\"Alice\"));\n" +
                        "> combineMaps(im1, im2);",
                () -> ImmutableMap.<String, User>empty().put("1", User.empty("1").ofName("Bob").ofAccountBalance(800)).put("2", User.empty("2").ofAccountBalance(900).ofName("Alice")),
                () -> {
                    ImmutableMap<String, User> im1 = ImmutableMap.<String, User>empty()
                            .put("1", User.empty("1")
                                    .ofName("Bob")
                                    .ofAccountBalance(1000))
                            .put("2", User.empty("2")
                                    .ofAccountBalance(-100));
                    ImmutableMap<String, User> im2 = ImmutableMap.<String, User>empty()
                            .put("1", User.empty("1")
                                    .ofAccountBalance(-200))
                            .put("2", User.empty("2")
                                    .ofAccountBalance(1000)
                                    .ofName("Alice"));
                    return StreamExercises.combineMaps(im1, im2);
                },
                1
        );
    }
}
