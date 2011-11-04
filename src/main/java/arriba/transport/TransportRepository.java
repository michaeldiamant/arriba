package arriba.transport;

public interface TransportRepository<ID, T> {

    Transport<T> add(ID id, Transport<T> transport);

    boolean remove(Transport<T> transport);

    Transport<T> find(ID id);
}
