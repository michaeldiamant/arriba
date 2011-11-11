package arriba.fix.session;

import arriba.common.Handler;
import arriba.common.HandlerRepository;
import arriba.common.NonexistentHandlerException;
import arriba.fix.inbound.InboundFixMessage;

public class Session {

    private final SessionId sessionId;
    private final HandlerRepository<String, ? extends InboundFixMessage> messageHandlerRepository;
    private int sequenceNumber = 0;

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

    public SessionId getSessionId() {
        return this.sessionId;
    }

    public int getNextSequenceNumber() {
        return this.sequenceNumber++;
    }

    public void resetSequenceNumber() {
        this.sequenceNumber = 0;
    }
}
