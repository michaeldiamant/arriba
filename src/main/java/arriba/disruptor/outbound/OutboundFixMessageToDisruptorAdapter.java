package arriba.disruptor.outbound;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.disruptor.SessionIdToDisruptorAdapter;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

public final class OutboundFixMessageToDisruptorAdapter implements
MessageToDisruptorAdapter<OutboundFixMessage, OutboundFixMessageEvent>,
SessionIdToDisruptorAdapter<OutboundFixMessageEvent> {

    @Override
    public void adapt(final OutboundFixMessage message, final OutboundFixMessageEvent event) {
        event.setFixMessage(message);
        event.setSessionId(null);
    }

    @Override
    public void adapt(final SessionId sessionId, final OutboundFixMessageEvent event) {
        event.setFixMessage(null);
        event.setSessionId(sessionId);
    }
}
