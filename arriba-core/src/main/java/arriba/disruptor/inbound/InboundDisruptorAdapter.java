package arriba.disruptor.inbound;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.disruptor.MessageToDisruptorAdapter;

public final class InboundDisruptorAdapter implements MessageToDisruptorAdapter<ChannelBuffer, InboundEvent>{

    public void adapt(final ChannelBuffer serializedFixMessage, final InboundEvent entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
