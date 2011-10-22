package arriba.disruptor.outbound;

import arriba.fix.OutboundFixMessage;

import com.lmax.disruptor.AbstractEvent;

public final class OutboundFixMessageEvent extends AbstractEvent {

    private OutboundFixMessage message;

    public OutboundFixMessageEvent() {}

    public OutboundFixMessage getMessage() {
        return this.message;
    }

    public void setMessage(final OutboundFixMessage message) {
        this.message = message;
    }
}
