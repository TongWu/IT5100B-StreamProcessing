package testingutils;

import java.util.function.Supplier;

public class UncomputedTestSet {
    String name;
    String description;
    String expression;
    Supplier<?> expected;
    double score;
    Supplier<?> output;
    public UncomputedTestSet(String name, String description, String expression, Supplier<?> expected, Supplier<?> outputSupplier, double score) {
        this.name = name;
        this.description = description;
        this.expression = expression;
        this.expected = expected;
        this.output = outputSupplier;
        this.score = score;
    }
    Object compute() { return output.get(); }
}
