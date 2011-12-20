package arriba.transport;

public interface TransportFactory<T> {

    Transport<T> create(TransportIdentity<T> identity);
}
