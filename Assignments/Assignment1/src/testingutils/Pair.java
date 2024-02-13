package testingutils;

public class Pair<T, U> {
    public final T fst;
    public final U snd;
    public static <T, U> Pair<T, U> of(T t, U u) {
        return new Pair<>(t, u);
    }
    private Pair(T t, U u) {
        fst = t;
        snd = u;
    }
    @Override
    public boolean equals(Object o) {
        return (o instanceof Pair)
                && ((Pair<?, ?>) o).fst.equals(fst)
                && ((Pair<?, ?>) o).snd.equals(snd);
    }
    @Override
    public String toString() {
        return String.format("(%s, %s)", fst, snd);
    }
}