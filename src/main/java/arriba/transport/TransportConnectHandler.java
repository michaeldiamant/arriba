package arriba.transport;

public interface TransportConnectHandler<T> {

    void onConnect(TransportIdentity<T> identity);
}
