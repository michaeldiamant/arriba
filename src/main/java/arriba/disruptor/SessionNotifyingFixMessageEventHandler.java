package arriba.disruptor;

import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SessionNotifyingFixMessageEventHandler implements EventHandler<FixMessageEvent> {

    private final SessionResolver sessionResolver;

    public SessionNotifyingFixMessageEventHandler(final SessionResolver sessionResolver) {
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void onEvent(final FixMessageEvent entry, final boolean endOfBatch) throws Exception {
        final InboundFixMessage inboundFixMessage = entry.getFixMessage();

        final Session session = this.sessionResolver.resolve(inboundFixMessage.getSessionId());

        session.onMessage(inboundFixMessage);
    }

    public void onEndOfBatch() throws Exception {}

}
