package arriba.disruptor.outbound;

import arriba.disruptor.CompIdToDisruptorAdapter;
import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.fix.outbound.OutboundFixMessage;

public final class OutboundFixMessageToDisruptorAdapter implements
MessageToDisruptorAdapter<OutboundFixMessage, OutboundFixMessageEvent>,
CompIdToDisruptorAdapter<OutboundFixMessageEvent> {

    @Override
    public void adapt(final OutboundFixMessage message, final OutboundFixMessageEvent event) {
        event.setFixMessage(message);
        event.setTargetCompId(null);
    }

    @Override
    public void adapt(final String compId, final OutboundFixMessageEvent event) {
        event.setFixMessage(null);
        event.setTargetCompId(compId);
    }
}
