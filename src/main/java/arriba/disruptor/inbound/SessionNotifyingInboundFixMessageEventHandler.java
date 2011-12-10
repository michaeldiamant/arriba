package arriba.disruptor.inbound;

import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SessionNotifyingInboundFixMessageEventHandler implements EventHandler<InboundEvent> {

    private final SessionResolver sessionResolver;

    public SessionNotifyingInboundFixMessageEventHandler(final SessionResolver sessionResolver) {
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void onEvent(final InboundEvent event, final boolean endOfBatch) throws Exception {
        final InboundFixMessage inboundFixMessage = event.getFixMessage();

        final Session session = this.sessionResolver.resolve(inboundFixMessage.getSessionId());
        if (null == session) {
            throw new IllegalArgumentException("No session found for " + inboundFixMessage.getSessionId());
        }

        session.updateLastReceivedTimestamp();

        session.onMessage(inboundFixMessage);
    }
}
