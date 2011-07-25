package arriba.fix.netty;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public final class InMemoryChannelRepository<ID> implements ChannelRepository<ID> {

    private final ConcurrentMap<ID, Channel> idToChannel = new ConcurrentHashMap<ID, Channel>();
    private final ChannelFutureListener removeChannelListener = new ChannelFutureListener() {
        public void operationComplete(final ChannelFuture future) throws Exception {
            InMemoryChannelRepository.this.remove(future.getChannel());
        }
    };

    public InMemoryChannelRepository() {}

    public void add(final ID id, final Channel channel) {
        final Channel previousChannel = this.idToChannel.putIfAbsent(id, channel);

        if (null == previousChannel) {
            channel.getCloseFuture().addListener(this.removeChannelListener);
        }
    }

    public void remove(final Channel channel) {
        final Iterator<Entry<ID, Channel>> iterator = this.idToChannel.entrySet().iterator();
        while (iterator.hasNext()) {
            if (channel == iterator.next().getValue()) {
                iterator.remove();

                return;
            }
        }
    }

    public void remove(final ID id) {
        this.idToChannel.remove(id);
    }

    public Channel find(final ID id) throws UnknownChannelIdException {
        final Channel channel = this.idToChannel.get(id);
        if (null == channel) {
            throw new UnknownChannelIdException("Received unknown channel ID: " + id + ".");
        }

        return channel;
    }
}
