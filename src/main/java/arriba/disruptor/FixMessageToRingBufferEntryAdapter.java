package arriba.disruptor;

import arriba.fix.inbound.FixMessage;

public final class FixMessageToRingBufferEntryAdapter implements MessageToDisruptorAdapter<FixMessage, FixMessageEvent>{

    public void adapt(final FixMessage fixMessage, final FixMessageEvent entry) {
        entry.setFixMessage(fixMessage);
    }
}
