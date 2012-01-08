package arriba.disruptor.outbound;

import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class MessageJournalingEventHandler implements EventHandler<OutboundEvent> {

    private final SessionResolver resolver;

    public MessageJournalingEventHandler(final SessionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void onEvent(final OutboundEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        if (!event.isResend()) {
            final Session session = this.resolver.resolve(event.getSessionId());
            session.journal(event.getSequenceNumber(), event.getSerializedFixMessage());
        }
    }
}
