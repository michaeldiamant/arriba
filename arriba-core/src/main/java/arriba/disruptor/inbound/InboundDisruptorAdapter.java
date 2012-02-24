package arriba.disruptor.inbound;

import arriba.disruptor.TransportMessageToDisruptorAdapter;
import arriba.transport.TransportIdentity;
import org.jboss.netty.buffer.ChannelBuffer;

import arriba.disruptor.MessageToDisruptorAdapter;

public final class InboundDisruptorAdapter<T> implements TransportMessageToDisruptorAdapter<T, ChannelBuffer[], InboundEvent> {

    @Override
    public void adapt(TransportIdentity<T> identity, ChannelBuffer[] serializedMessages, InboundEvent event) {
        event.setIdentity(identity);
        event.setSerializedMessages(serializedMessages);
    }
}
