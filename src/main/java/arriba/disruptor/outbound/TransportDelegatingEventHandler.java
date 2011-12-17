package arriba.disruptor.outbound;

import arriba.common.Handler;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

import com.lmax.disruptor.EventHandler;

public final class TransportDelegatingEventHandler<T> implements EventHandler<OutboundEvent> {

    private final Handler<OutboundFixMessage> fixMessageHandler;
    private final Handler<SessionId> sessionIdHandler;

    public TransportDelegatingEventHandler(final Handler<OutboundFixMessage> fixMessageHandler,
            final Handler<SessionId> sessionIdHandler) {
        this.fixMessageHandler = fixMessageHandler;
        this.sessionIdHandler = sessionIdHandler;
    }

    @Override
    public void onEvent(final OutboundEvent entry, final boolean endOfBatch) throws Exception {
        if (null != entry.getFixMessage()) {
            this.fixMessageHandler.handle(entry.getFixMessage());
        }

        if (null != entry.getSessionId()) {
            this.sessionIdHandler.handle(entry.getSessionId());
        }
    }
}
