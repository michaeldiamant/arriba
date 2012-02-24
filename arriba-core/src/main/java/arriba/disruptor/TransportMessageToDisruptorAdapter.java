package arriba.disruptor;

import arriba.transport.TransportIdentity;

public interface TransportMessageToDisruptorAdapter<T, M, E> {

    void adapt(TransportIdentity<T> identity, M message, E event);
}
