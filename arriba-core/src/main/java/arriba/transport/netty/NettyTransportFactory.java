package arriba.transport.netty;

import org.jboss.netty.channel.Channel;

import arriba.transport.Transport;
import arriba.transport.TransportFactory;
import arriba.transport.TransportIdentity;

public final class NettyTransportFactory implements TransportFactory<Channel> {

    @Override
    public Transport<Channel> create(final TransportIdentity<Channel> identity) {
        return new NettyTransport(identity);
    }
}
