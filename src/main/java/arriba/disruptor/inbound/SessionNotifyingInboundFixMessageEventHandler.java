package arriba.disruptor.inbound;

import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SessionNotifyingInboundFixMessageEventHandler implements EventHandler<InboundFixMessageEvent> {

    private final SessionResolver sessionResolver;

    public SessionNotifyingInboundFixMessageEventHandler(final SessionResolver sessionResolver) {
        this.sessionResolver = sessionResolver;
    }

    @Override
    public void onEvent(final InboundFixMessageEvent event, final boolean endOfBatch) throws Exception {
        final InboundFixMessage inboundFixMessage = event.getFixMessage();

        final Session session = this.sessionResolver.resolve(inboundFixMessage.getSessionId());

        session.onMessage(inboundFixMessage);
    }

    public void onEndOfBatch() throws Exception {}

}
