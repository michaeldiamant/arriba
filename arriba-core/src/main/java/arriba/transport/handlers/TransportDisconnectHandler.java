package arriba.transport.handlers;

import arriba.transport.TransportIdentity;

public interface TransportDisconnectHandler<T> {

    void onDisconnect(TransportIdentity<T> identity);
}
