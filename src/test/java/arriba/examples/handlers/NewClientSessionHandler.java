package arriba.examples.handlers;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class NewClientSessionHandler extends SimpleChannelHandler {

    private final List<Channel> channels;

    public NewClientSessionHandler(final List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.channels.add(e.getChannel());
    }
}
