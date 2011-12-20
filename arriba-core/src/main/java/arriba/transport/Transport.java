package arriba.transport;

public abstract class Transport<T> {

    private final TransportIdentity<T> identity;

    public Transport(final TransportIdentity<T> identity) {
        this.identity = identity;
    }

    public TransportIdentity<T> getIdentity() {
        return this.identity;
    }

    public T getUnderlying() {
        return this.identity.getUnderlying();
    }

    public abstract void write(byte[] bytes);

    public abstract void close();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.identity.hashCode();
        return result;
    }

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

        @SuppressWarnings("rawtypes")
        final Transport other = (Transport) obj;
        return this.identity.equals(other.identity);
    }
}
