package historical;

import testingutils.Pair;
import testingutils.UncomputedTestSet;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static testingutils.Tester.testAll;

public class HistoricalTest {
    public static void main(String[] args) {
        testAll(List.of(
                HistoricalTest::public_test1,
                HistoricalTest::public_test2,
                HistoricalTest::public_test3,
                HistoricalTest::public_test4,
                HistoricalTest::public_test5,
                HistoricalTest::public_test6,
                HistoricalTest::public_test7,
                HistoricalTest::public_test8,
                HistoricalTest::public_test9,
                HistoricalTest::public_test10,
                HistoricalTest::public_test11,
                HistoricalTest::public_test12
        ));
    }


    private static UncomputedTestSet public_test1() {

        return new UncomputedTestSet(
                "of() and get() Test 1",
                "of(\"hello\") should initialize a Historical object containing \"hello\"",
                "> Historical<String> h1 = Historical.of(\"hello\");\n" +
                        "> Optional<String> s = h1.get();\n" +
                        "> s.get()",
                () -> "hello",
                () -> {
                    Historical<String> h1 = Historical.of("hello");
                    Optional<String> s = h1.get();
                    return s.get();
                },
                1);
    }

    private static UncomputedTestSet public_test2() {
        return new UncomputedTestSet(
                "of() and get() Test 2",
                "of(null) should initialize a Historical object containing nothing",
                "> Historical<String> h1 = Historical.of(null);\n" +
                        "> h1.get()",
                Optional::<String>empty,
                () -> {
                    Historical<String> h1 = Historical.of(null);
                    return h1.get();
                },
                1);
    }

    private static UncomputedTestSet public_test3() {
        return new UncomputedTestSet(
                "replace() Test 1",
                "replace() should replace the current value with a new value",
                "> Historical<String> h1 = Historical.of(null);\n" +
                        "> Historical<Integer> h2 = h1.replace(2);\n" +
                        "> h1.get().equals(Optional.empty()) && h2.get().equals(Optional.of(2))",
                () -> true,
                () -> {
                    Historical<String> h1 = Historical.of(null);
                    Historical<Integer> h2 = h1.replace(2);
                    return h1.get().equals(Optional.empty()) && h2.get().equals(Optional.of(2));
                },
                0.5);
    }

    private static UncomputedTestSet public_test4() {
        return new UncomputedTestSet(
                "replace() Test 2",
                "replace() should not produce consecutive duplicates",
                "> Historical.of(1).replace(1).replace(null).replace(null)",
                () -> Historical.of(1).replace(null),
                () -> Historical.of(1).replace(1).replace(null).replace(null),
                0.5);
    }

    private static UncomputedTestSet public_test5() {
        return new UncomputedTestSet(
                "map() Test 1",
                "map() should map the current value into something else",
                "> Historical.of(1).map(x -> x + 1)",
                () -> Historical.of(1).replace(2),
                () -> Historical.of(1).map(x -> x + 1),
                0.5);
    }

    private static UncomputedTestSet public_test6() {
        return new UncomputedTestSet(
                "map() Test 2",
                "map() should do nothing if there is no current value",
                "> Historical.of(1).replace(null).map(Object::toString)",
                () -> Historical.of(1).replace(null),
                () -> Historical.of(1).replace(null).map(Object::toString),
                0.5);
    }
    private static UncomputedTestSet public_test7() {
        return new UncomputedTestSet(
                "filter() Test 1",
                "filter() should remove an element if it fails the predicate",
                "> Historical.of(1).filter(x -> x > 1)",
                () -> Historical.of(1).replace(null),
                () -> Historical.of(1).filter(x -> x > 1),
                0.5);
    }

    private static UncomputedTestSet public_test8() {
        return new UncomputedTestSet(
                "filter() Test 2",
                "filter() should do nothing if there is no current value or if the current value passes the predicate",
                "> (Historical.of(1).replace(\"hello\").filter(x -> x.length() == 5), Historical.of(2).replace(null).filter(x -> x.equals(\"lol\")))",
                () -> Pair.of(Historical.of(1).replace("hello"),
                        Historical.of(2).replace(null)),
                () -> Pair.of(Historical.of(1).replace("hello").filter(x -> x.length() == 5),
                        Historical.of(2).replace(null).filter(x -> x.equals("lol"))),
                0.5);
    }
    private static UncomputedTestSet public_test9() {
        return new UncomputedTestSet(
                "flatMap() Test 1",
                "flatMap() should concatenate two histories",
                "> Function<String, Historical<Integer>> f = x -> Historical.of(x).map(String::length).map(y -> y + 3);\n" +
                        "> Historical.of(1).replace(\"hello!!\").flatMap(f)",
                () -> Historical.of(1).replace("hello!!").replace(7).replace(10),
                () -> {
                    Function<String, Historical<Integer>> f = x -> Historical.of(x).map(String::length).map(y -> y + 3);
                    return Historical.of(1).replace("hello!!").flatMap(f);
                },
                0.5);
    }

    private static UncomputedTestSet public_test10() {
        return new UncomputedTestSet(
                "flatMap() Test 2",
                "flatMap() should do nothing if there is no current value",
                "> Function<String, Historical<Integer>> f = x -> Historical.of(x).map(String::length).map(y -> y + 3);\n" +
                        "> Historical.of(1).<String>replace(null).flatMap(f)",
                () -> Historical.of(1).replace(null),
                () -> {
                    Function<String, Historical<Integer>> f = x -> Historical.of(x).map(String::length).map(y -> y + 3);
                    return Historical.of(1).<String>replace(null).flatMap(f);
                },
                0.5);
    }

    private static UncomputedTestSet public_test11() {
        return new UncomputedTestSet(
                "undo() Test 1",
                "undo() should give the previous history",
                "> Historical.of(1).replace(\"hello!\").undo()",
                () -> Optional.of(Historical.of(1)),
                () -> Historical.of(1).replace("hello!").undo(),
                0.5);
    }

    private static UncomputedTestSet public_test12() {
        return new UncomputedTestSet(
                "undo() Test 2",
                "undo() should return Optional.empty if it has no history",
                "> Historical.of(1).undo()",
                Optional::empty,
                () -> Historical.of(1).undo(),
                0.5);
    }

}

