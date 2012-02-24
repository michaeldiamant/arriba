package arriba.transport.netty;

import arriba.transport.TransportIdentity;
import arriba.transport.TransportSender;
import org.jboss.netty.channel.*;
import org.jboss.netty.buffer.ChannelBuffer;

import arriba.common.Sender;

public final class SerializedFixMessageHandler extends SimpleChannelHandler {

    private final TransportSender<Channel, ChannelBuffer[]> sender;
    private TransportIdentity<Channel> identity = null;

    public SerializedFixMessageHandler(final TransportSender<Channel, ChannelBuffer[]> sender) {
        this.sender = sender;
    }

    
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        this.identity = new TransportIdentity<Channel>(ctx.getChannel());
        super.channelConnected(ctx, e);
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        this.sender.send(this.identity, (ChannelBuffer[]) e.getMessage());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        e.getChannel().close();

        // FIXME Log an error.
    }
}
