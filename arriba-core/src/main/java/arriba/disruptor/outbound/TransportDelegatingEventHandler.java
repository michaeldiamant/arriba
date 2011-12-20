package arriba.disruptor.outbound;

import arriba.common.Handler;
import arriba.fix.session.SessionId;

import com.lmax.disruptor.EventHandler;

public final class TransportDelegatingEventHandler<T> implements EventHandler<OutboundEvent> {

    private final Handler<OutboundEvent> fixMessageHandler;
    private final Handler<SessionId> sessionIdHandler;

    public TransportDelegatingEventHandler(final Handler<OutboundEvent> fixMessageHandler,
            final Handler<SessionId> sessionIdHandler) {
        this.fixMessageHandler = fixMessageHandler;
        this.sessionIdHandler = sessionIdHandler;
    }

    @Override
    public void onEvent(final OutboundEvent event, final boolean endOfBatch) throws Exception {
        if (null != event.getFixMessage()) {
            this.fixMessageHandler.handle(event);
        }

        if (null != event.getSessionId()) {
            this.sessionIdHandler.handle(event.getSessionId());
        }
    }
}
