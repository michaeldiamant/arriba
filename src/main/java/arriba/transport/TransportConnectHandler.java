package arriba.transport;

public interface TransportConnectHandler<T> {

    void onConnect(Transport<T> transport);
}
