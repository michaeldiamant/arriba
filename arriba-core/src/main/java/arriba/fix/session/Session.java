package arriba.fix.session;

import arriba.common.Handler;
import arriba.common.HandlerRepository;
import arriba.common.NonexistentHandlerException;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.session.messagejournal.MessageJournal;

public class Session {

    private final SessionId sessionId;
    private final HandlerRepository<String, ? extends InboundFixMessage> messageHandlerRepository;
    private final MessageJournal journal;
    private int outboundSequenceNumber = 0;
    private long lastReceivedTimestamp = 0;
    private long lastSentTimestamp = 0;

    public Session(final SessionId sessionId,
            final HandlerRepository<String, ? extends InboundFixMessage> messageHandlerRepository,
            final MessageJournal journal) {
        this.sessionId = sessionId;
        this.messageHandlerRepository = messageHandlerRepository;
        this.journal = journal;
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

    public long getLastSentTimestamp() {
        return this.lastSentTimestamp;
    }

    public void updateLastSentTimestamp() {
        this.lastSentTimestamp = System.currentTimeMillis();
    }

    public void updateLastReceivedTimestamp() {
        this.lastReceivedTimestamp = System.currentTimeMillis();
    }

    public String getSenderCompId() {
        return this.sessionId.getSenderCompId();
    }

    public String getTargetCompId() {
        return this.sessionId.getTargetCompId();
    }

    public int getNextOutboundSequenceNumber() {
        return this.outboundSequenceNumber++;
    }

    public void resetOutboundSequenceNumber() {
        this.outboundSequenceNumber = 0;
    }

    public void journal(final int sequenceNumber, final byte[] message) {
        this.journal.write(sequenceNumber, message);
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
