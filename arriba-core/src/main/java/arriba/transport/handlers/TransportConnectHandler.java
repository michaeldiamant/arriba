package arriba.transport.handlers;

import arriba.transport.TransportIdentity;

public interface TransportConnectHandler<T> {

    void onConnect(TransportIdentity<T> identity);
}
