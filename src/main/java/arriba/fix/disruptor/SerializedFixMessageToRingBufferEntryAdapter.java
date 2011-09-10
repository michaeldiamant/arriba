package arriba.fix.disruptor;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.common.MessageToRingBufferEntryAdapter;

public final class SerializedFixMessageToRingBufferEntryAdapter implements MessageToRingBufferEntryAdapter<ChannelBuffer, FixMessageEvent>{

    public void adapt(final ChannelBuffer serializedFixMessage, final FixMessageEvent entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
