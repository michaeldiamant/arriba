package arriba.fix.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import arriba.common.Sender;
import arriba.fix.messages.FixMessage;

public final class FixMessageHandler extends SimpleChannelHandler {

    private final Sender<FixMessage> ringBufferSender;

    public FixMessageHandler(final Sender<FixMessage> ringBufferSender) {
        this.ringBufferSender = ringBufferSender;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        final FixMessage fixMessage = (FixMessage) e.getMessage();

        this.ringBufferSender.send(fixMessage);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        e.getChannel().close();

        // FIXME Log an error.
    }
}
