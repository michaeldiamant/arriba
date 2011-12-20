package arriba.transport.netty;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.disruptor.inbound.InboundEvent;

public final class SerializedFixMessageToDisruptorAdapter implements MessageToDisruptorAdapter<ChannelBuffer, InboundEvent>{

    public void adapt(final ChannelBuffer serializedFixMessage, final InboundEvent entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
