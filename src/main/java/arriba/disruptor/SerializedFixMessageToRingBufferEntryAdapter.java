package arriba.disruptor;

import org.jboss.netty.buffer.ChannelBuffer;


public final class SerializedFixMessageToRingBufferEntryAdapter implements MessageToDisruptorAdapter<ChannelBuffer, FixMessageEvent>{

    public void adapt(final ChannelBuffer serializedFixMessage, final FixMessageEvent entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
