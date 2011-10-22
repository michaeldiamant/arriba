package arriba.disruptor.outbound;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.fix.OutboundFixMessage;

public final class OutboundFixMessageToDisruptorAdapter implements MessageToDisruptorAdapter<OutboundFixMessage, OutboundFixMessageEvent> {

    @Override
    public void adapt(final OutboundFixMessage message, final OutboundFixMessageEvent event) {
        event.setMessage(message);
    }
}
