package arriba.transport.netty;

import arriba.transport.TransportIdentity;
import arriba.transport.handlers.TransportDisconnectHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.Channel;

public final class NettyDisconnectHandlerAdapter extends SimpleChannelHandler {

    private final TransportDisconnectHandler<Channel> handler;

    public NettyDisconnectHandlerAdapter(TransportDisconnectHandler<Channel> handler) {
        this.handler = handler;
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        handler.onDisconnect(new TransportIdentity<>(e.getChannel()));

        super.channelDisconnected(ctx, e);
    }
}