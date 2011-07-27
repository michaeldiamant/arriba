package arriba.fix.disruptor;

import arriba.common.MessageToRingBufferEntryAdapter;

public final class SerializedFixMessageToRingBufferEntryAdapter implements MessageToRingBufferEntryAdapter<byte[], FixMessageEntry>{

    public void adapt(final byte[] serializedFixMessage, final FixMessageEntry entry) {
        entry.setSerializedFixMessage(serializedFixMessage);
    }
}
