package arriba.disruptor.outbound;

import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

import com.lmax.disruptor.AbstractEvent;

public final class OutboundEvent extends AbstractEvent {

    private OutboundFixMessage message;
    private SessionId sessionId;
    private byte[] serializedFixMessage;

    public OutboundEvent() {}

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

    public byte[] getSerializedFixMessage() {
        return this.serializedFixMessage;
    }

    public void setSerializedFixMessage(final byte[] serializedFixMessage) {
        this.serializedFixMessage = serializedFixMessage;
    }
}
