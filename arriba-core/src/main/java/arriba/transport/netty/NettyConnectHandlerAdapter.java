package arriba.transport.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import arriba.transport.TransportIdentity;
import arriba.transport.handlers.TransportConnectHandler;

public final class NettyConnectHandlerAdapter extends SimpleChannelHandler {

    private final TransportConnectHandler<Channel> connectHandler;

    public NettyConnectHandlerAdapter(final TransportConnectHandler<Channel> connectHandler) {
        this.connectHandler = connectHandler;
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.connectHandler.onConnect(new TransportIdentity<>(e.getChannel()));
    }
}
