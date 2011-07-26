package arriba.fix.netty;

import java.util.List;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import arriba.common.Sender;
import arriba.fix.SerializedField;

public final class SerializedFieldHandler extends SimpleChannelHandler {

    private final Sender<List<SerializedField>> ringBufferSender;

    public SerializedFieldHandler(final Sender<List<SerializedField>> ringBufferSender) {
        this.ringBufferSender = ringBufferSender;
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        @SuppressWarnings("unchecked")
        final List<SerializedField> fieldsAndValues = (List<SerializedField>) e.getMessage();

        this.ringBufferSender.send(fieldsAndValues);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        e.getChannel().close();

        // FIXME Log an error.
    }
}
