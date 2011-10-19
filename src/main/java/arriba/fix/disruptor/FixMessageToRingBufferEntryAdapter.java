package arriba.fix.disruptor;

import arriba.common.MessageToRingBufferEntryAdapter;
import arriba.fix.messages.FixMessage;

public final class FixMessageToRingBufferEntryAdapter implements MessageToRingBufferEntryAdapter<FixMessage, FixMessageEvent>{

    public void adapt(final FixMessage fixMessage, final FixMessageEvent entry) {
        entry.setFixMessage(fixMessage);
    }
}
