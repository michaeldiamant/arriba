package arriba.disruptor.outbound;

import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

public final class OutboundEvent {

    private OutboundFixMessage message;
    private SessionId sessionId;
    private byte[] serializedFixMessage;
    private boolean isResend;
    private int sequenceNumber;

    public OutboundEvent() {}

    public OutboundFixMessage getFixMessage() {
        return this.message;
    }

    public boolean isResend() {
        return this.isResend;
    }

    public void setResend(final boolean isResend) {
        this.isResend = isResend;
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

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public void setSequenceNumber(final int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
