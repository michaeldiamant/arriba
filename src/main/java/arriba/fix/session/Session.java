package arriba.fix.session;

import arriba.common.Handler;
import arriba.common.HandlerRepository;
import arriba.common.NonexistentHandlerException;
import arriba.fix.inbound.InboundFixMessage;

public class Session {

    private final SessionId sessionId;
    private final HandlerRepository<String, ? extends InboundFixMessage> messageHandlerRepository;
    private int sequenceNumber = 0;
    private long lastReceivedTimestamp = 0;
    private long lastSentTimestamp = 0;

    public Session(final SessionId sessionId,
            final HandlerRepository<String, ? extends InboundFixMessage> messageHandlerRepository) {
        this.sessionId = sessionId;
        this.messageHandlerRepository = messageHandlerRepository;
    }

    @SuppressWarnings("unchecked")
    public <T extends InboundFixMessage > void onMessage(final T fixMessage) throws MessageHandlingException {
        final Handler<T> handler;
        try {
            handler = (Handler<T>) this.messageHandlerRepository.findHandler(fixMessage.getMessageType());
        } catch (final NonexistentHandlerException e) {
            throw new MessageHandlingException(e);
        }

        handler.handle(fixMessage);
    }

    public long getLastReceivedTimestamp() {
        return this.lastReceivedTimestamp;
    }

    public void setLastReceivedTimestamp(final long lastReceivedTimestamp) {
        this.lastReceivedTimestamp = lastReceivedTimestamp;
    }

    public long getLastSentTimestamp() {
        return this.lastSentTimestamp;
    }

    public void setLastSentTimestamp(final long lastSentTimestamp) {
        this.lastSentTimestamp = lastSentTimestamp;
    }

    public String getSenderCompId() {
        return this.sessionId.getSenderCompId();
    }

    public String getTargetCompId() {
        return this.sessionId.getTargetCompId();
    }

    public int getNextSequenceNumber() {
        return this.sequenceNumber++;
    }

    public void resetSequenceNumber() {
        this.sequenceNumber = 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.sessionId.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        return this.sessionId.equals(((Session) obj).sessionId);
    }
}
