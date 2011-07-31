package arriba.fix.disruptor;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.common.MessageToRingBufferEntryAdapter;

public final class SerializedFixMessageToRingBufferEntryAdapter implements MessageToRingBufferEntryAdapter<ChannelBuffer, FixMessageEntry>{

    public void adapt(final ChannelBuffer serializedFixMessage, final FixMessageEntry entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
