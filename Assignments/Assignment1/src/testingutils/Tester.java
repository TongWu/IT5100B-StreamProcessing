package testingutils;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Tester {
    public static ComputedTestSet test(Supplier<UncomputedTestSet> f) {
        UncomputedTestSet u = f.get();
        try {
            Object output = u.compute();
            return new ComputedTestSet(u.name, u.description, u.expression, u.expected.get(), output, u.score);
        } catch (Exception e) {
            return new ComputedTestSet(u.name, u.description, u.expression, "--- exception occurred ---", e.getMessage(), u.score);
        }
    }

    public static void testAll(List<Supplier<UncomputedTestSet>> tests) {
        List<ComputedTestSet> ls = tests.stream()
                .map(Tester::test)
                .collect(Collectors.toList());
        double attainedScore = ls.stream().mapToDouble(x -> x.attainedScore).sum();
        double maxScore = ls.stream().mapToDouble(x -> x.maxScore).sum();
        ls.forEach(System.out::println);
        System.out.printf("Total attained score: %.1f/%.1f\nTEST COMPLETE\n", attainedScore, maxScore);
    }
}
