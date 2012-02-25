package arriba.transport;

public interface TransportRepository<ID, T> {

    Transport<T> add(ID id, TransportIdentity<T> identity);

    boolean remove(TransportIdentity<T> identity);

    Transport<T> find(ID id);
    
    ID find(TransportIdentity<T> identity);
}
