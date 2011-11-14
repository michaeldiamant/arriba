package arriba.transport.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import arriba.transport.Transport;
import arriba.transport.TransportIdentity;

public final class NettyTransport extends Transport<Channel> {

    public NettyTransport(final TransportIdentity<Channel> identity) {
        super(identity);
    }

    @Override
    public void write(final byte[] bytes) {
        final ChannelBuffer messageBuffer = ChannelBuffers.copiedBuffer(bytes);
        this.getUnderlying().write(messageBuffer);
    }
}
