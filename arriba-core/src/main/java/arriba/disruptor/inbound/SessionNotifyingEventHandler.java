package arriba.disruptor.inbound;

import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.session.MessageHandlingException;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SessionNotifyingEventHandler implements EventHandler<InboundEvent> {

    private final SessionResolver resolver;

    public SessionNotifyingEventHandler(final SessionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void onEvent(final InboundEvent event, final boolean endOfBatch) throws Exception {
        final InboundFixMessage[] messages = event.getMessages();
        for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
            this.notifySession(messages[messageIndex]);
        }
    }

    private void notifySession(final InboundFixMessage inboundFixMessage) throws MessageHandlingException {
        final Session session = this.resolver.resolve(inboundFixMessage.getSessionId());
        if (null == session) {
            throw new IllegalArgumentException("No session found for " + inboundFixMessage.getSessionId());
        }

        session.onMessage(inboundFixMessage);
    }
}
