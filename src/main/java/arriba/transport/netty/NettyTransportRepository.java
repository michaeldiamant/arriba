package arriba.transport.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import arriba.transport.Transport;
import arriba.transport.TransportRepository;

public final class NettyTransportRepository<ID> implements TransportRepository<ID, Channel> {

    private final TransportRepository<ID, Channel> backingRepository;
    private final ChannelFutureListener removeChannelListener = new ChannelFutureListener() {
        public void operationComplete(final ChannelFuture future) throws Exception {

            // TODO Consider creating a read-only Transport implementation
            NettyTransportRepository.this.remove(new NettyTransport(future.getChannel()));
        }
    };

    public NettyTransportRepository(final TransportRepository<ID, Channel> backingRepository) {
        this.backingRepository = backingRepository;
    }

    @Override
    public Transport<Channel> add(final ID id, final Transport<Channel> transport) {
        final Transport<Channel> previousTransport = this.backingRepository.add(id, transport);

        if (null == previousTransport) {
            transport.getUnderlying().getCloseFuture().addListener(this.removeChannelListener);
        }

        return previousTransport;
    }

    @Override
    public boolean remove(final Transport<Channel> transport) {
        return this.backingRepository.remove(transport);
    }

    @Override
    public Transport<Channel> find(final ID id) {
        return this.backingRepository.find(id);
    }


}
