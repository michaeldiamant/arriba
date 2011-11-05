package arriba.transport.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import arriba.transport.Transport;

public final class NettyTransport extends Transport<Channel> {

    public NettyTransport(final Channel underlying) {
        super(underlying);
    }

    @Override
    public void write(final byte[] bytes) {
        final ChannelBuffer messageBuffer = ChannelBuffers.copiedBuffer(bytes);
        this.underlying.write(messageBuffer);
    }
}
