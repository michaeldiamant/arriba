package arriba.common;

public final class Tuple<F, S> {

    private final F first;
    private final S second;

    public Tuple(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return this.first;
    }

    public S getSecond() {
        return this.second;
    }

    @Override
    public String toString() {
        return "[" + this.first + ", " + this.second + "]";
    }
}
