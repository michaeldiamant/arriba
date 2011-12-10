package arriba.disruptor.outbound;

import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

import com.lmax.disruptor.AbstractEvent;

public final class OutboundFixMessageEvent extends AbstractEvent {

    private OutboundFixMessage message;
    private SessionId sessionId;

    public OutboundFixMessageEvent() {}

    public OutboundFixMessage getFixMessage() {
        return this.message;
    }

    public void setFixMessage(final OutboundFixMessage message) {
        this.message = message;
    }

    public SessionId getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(final SessionId sessionId) {
        this.sessionId = sessionId;
    }
}
