package arriba.disruptor.inbound;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.fix.inbound.InboundFixMessage;

public final class InboundFixMessageToDisruptorAdapter implements MessageToDisruptorAdapter<InboundFixMessage, InboundFixMessageEvent> {

    public void adapt(final InboundFixMessage inboundFixMessage, final InboundFixMessageEvent entry) {
        entry.setFixMessage(inboundFixMessage);
    }
}
