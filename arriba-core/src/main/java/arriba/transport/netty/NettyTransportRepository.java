package arriba.transport.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import arriba.transport.Transport;
import arriba.transport.TransportIdentity;
import arriba.transport.TransportRepository;

public final class NettyTransportRepository<ID> implements TransportRepository<ID, Channel> {

    private final TransportRepository<ID, Channel> backingRepository;
    private final ChannelFutureListener removeChannelListener = new ChannelFutureListener() {
        public void operationComplete(final ChannelFuture future) throws Exception {

            // TODO Consider creating a read-only Transport implementation
            NettyTransportRepository.this.remove(new TransportIdentity<>(future.getChannel()));
        }
    };

    public NettyTransportRepository(final TransportRepository<ID, Channel> backingRepository) {
        this.backingRepository = backingRepository;
    }

    @Override
    public Transport<Channel> add(final ID id, final TransportIdentity<Channel> identity) {
        final Transport<Channel> previousTransport = this.backingRepository.add(id, identity);

        if (null == previousTransport) {
            identity.getUnderlying().getCloseFuture().addListener(this.removeChannelListener);
        }

        return previousTransport;
    }

    @Override
    public boolean remove(final TransportIdentity<Channel> identity) {
        return this.backingRepository.remove(identity);
    }

    @Override
    public Transport<Channel> find(final ID id) {
        return this.backingRepository.find(id);
    }

    @Override
    public ID find(TransportIdentity<Channel> identity) {
        return this.backingRepository.find(identity);
    }
}
