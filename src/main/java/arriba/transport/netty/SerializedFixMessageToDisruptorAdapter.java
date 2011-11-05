package arriba.transport.netty;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.disruptor.inbound.InboundFixMessageEvent;

public final class SerializedFixMessageToDisruptorAdapter implements MessageToDisruptorAdapter<ChannelBuffer, InboundFixMessageEvent>{

    public void adapt(final ChannelBuffer serializedFixMessage, final InboundFixMessageEvent entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
