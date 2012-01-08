package arriba.disruptor.outbound;

import arriba.disruptor.MessageToDisruptorAdapter;
import arriba.disruptor.SessionIdToDisruptorAdapter;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionIds;

public final class OutboundDisruptorAdapter implements
MessageToDisruptorAdapter<OutboundFixMessage, OutboundEvent>,
SessionIdToDisruptorAdapter<OutboundEvent> {

    @Override
    public void adapt(final OutboundFixMessage message, final OutboundEvent event) {
        event.setFixMessage(message);
        event.setSessionId(SessionIds.newSessionId(message));
        event.setSerializedFixMessage(null);
        event.setResend(false);
    }

    @Override
    public void adapt(final SessionId sessionId, final OutboundEvent event) {
        event.setFixMessage(null);
        event.setSessionId(sessionId);
        event.setSerializedFixMessage(null);
        event.setResend(false);
    }
}
