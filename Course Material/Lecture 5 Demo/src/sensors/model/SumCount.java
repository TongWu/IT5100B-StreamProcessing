package sensors.model;

public class SumCount {
    public final double sum;
    public final long count;

    public SumCount(double s, long c) {
        sum = s;
        count = c;
    }

    public SumCount() {
        this(0, 0);
    }

    public double get() {
        return sum / count;
    }

    public SumCount put(double d) {
        return new SumCount(sum + d, count + 1);
    }

}
