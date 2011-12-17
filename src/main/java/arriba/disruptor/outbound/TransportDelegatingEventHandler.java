package arriba.disruptor.outbound;

import arriba.common.Handler;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.session.SessionId;

import com.google.common.base.Function;
import com.lmax.disruptor.EventHandler;

public final class TransportDelegatingEventHandler<T> implements EventHandler<OutboundEvent> {

    private final Function<OutboundFixMessage, byte[]> fixMessageFunc;
    private final Handler<SessionId> sessionIdHandler;

    public TransportDelegatingEventHandler(final Function<OutboundFixMessage, byte[]> fixMessageHandler,
            final Handler<SessionId> sessionIdHandler) {
        this.fixMessageFunc = fixMessageHandler;
        this.sessionIdHandler = sessionIdHandler;
    }

    @Override
    public void onEvent(final OutboundEvent event, final boolean endOfBatch) throws Exception {
        if (null != event.getFixMessage()) {
            final byte[] messageBytes = this.fixMessageFunc.apply(event.getFixMessage());
            event.setSerializedFixMessage(messageBytes);
        }

        if (null != event.getSessionId()) {
            this.sessionIdHandler.handle(event.getSessionId());
        }
    }
}
