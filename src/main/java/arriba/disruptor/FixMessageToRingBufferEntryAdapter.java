package arriba.disruptor;

import arriba.fix.inbound.InboundFixMessage;

public final class FixMessageToRingBufferEntryAdapter implements MessageToDisruptorAdapter<InboundFixMessage, FixMessageEvent>{

    public void adapt(final InboundFixMessage inboundFixMessage, final FixMessageEvent entry) {
        entry.setFixMessage(inboundFixMessage);
    }
}
