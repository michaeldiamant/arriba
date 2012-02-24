package arriba.transport;

public interface TransportSender<T, M> {
    
    void send(TransportIdentity<T> identity, M message);
}

