package arriba.disruptor.outbound;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.disruptor.SessionIdToDisruptorAdapter;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

public final class OutboundDisruptorAdapter implements
MessageToDisruptorAdapter<OutboundFixMessage, OutboundEvent>,
SessionIdToDisruptorAdapter<OutboundEvent> {

    @Override
    public void adapt(final OutboundFixMessage message, final OutboundEvent event) {
        event.setFixMessage(message);
        event.setSessionId(null);
    }

    @Override
    public void adapt(final SessionId sessionId, final OutboundEvent event) {
        event.setFixMessage(null);
        event.setSessionId(sessionId);
    }
}
