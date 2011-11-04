package arriba.transport;

public final class Transport<T> {

    private final T underlying;

    public Transport(final T underlying) {
        this.underlying = underlying;
    }

    public T getUnderlying() {
        return this.underlying;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.underlying == null) ? 0 : this.underlying.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        return this.underlying.equals(((Transport) obj).underlying);
    }
}
