package arriba.transport;

public interface TransportRepository<ID, T> {

    Transport<T> add(ID id, TransportIdentity<T> transport);

    boolean remove(TransportIdentity<T> transport);

    Transport<T> find(ID id);
}
