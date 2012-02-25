package arriba.transport.netty;

import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;

public class NewClientSessionHandler extends SimpleChannelHandler {

    private final ChannelGroup group;
    
    public NewClientSessionHandler(ChannelGroup group) {
        this.group = group;
    }

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.group.add(e.getChannel());

        super.channelConnected(ctx, e);
    }
}
