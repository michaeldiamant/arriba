package arriba.transport.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import arriba.common.Sender;

public final class SerializedFixMessageHandler extends SimpleChannelHandler {

    private final Sender<ChannelBuffer> disruptorSender;

    public SerializedFixMessageHandler(final Sender<ChannelBuffer> disruptorSender) {
        this.disruptorSender = disruptorSender;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        this.disruptorSender.send((ChannelBuffer) e.getMessage());
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        e.getChannel().close();

        // FIXME Log an error.
    }
}
