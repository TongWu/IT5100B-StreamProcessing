package testingutils;

public class ComputedTestSet {
    String name;
    String description;
    String expression;
    Object expected;
    Object output;
    double maxScore;
    double attainedScore;
    ComputedTestSet(String name, String description, String expression, Object expected, Object output, double maxScore) {
        this.name = name;
        this.description = description;
        this.expression = expression;
        this.expected = expected;
        this.output = output;
        this.maxScore = maxScore;
        if (expected.equals(output))
            this.attainedScore = maxScore;
        else
            this.attainedScore = 0;
    }
    @Override
    public String toString() {
        return String.format("===== %s =====\n%s\n\nTest:\n%s\n\nExpected: %s\nOutput:   %s\nAttained score: %.1f/%.1f\n================================", name, description, expression, expected, output, attainedScore, maxScore);
    }
}