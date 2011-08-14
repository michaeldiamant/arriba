package arriba.fix.disruptor;

import arriba.common.MessageToRingBufferEntryAdapter;
import arriba.fix.messages.FixMessage;

public final class FixMessageToRingBufferEntryAdapter implements MessageToRingBufferEntryAdapter<FixMessage, FixMessageEntry>{

    public void adapt(final FixMessage fixMessage, final FixMessageEntry entry) {
        entry.setFixMessage(fixMessage);
    }
}
