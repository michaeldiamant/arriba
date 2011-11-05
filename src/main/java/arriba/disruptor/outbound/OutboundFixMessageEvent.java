package arriba.disruptor.outbound;

import arriba.fix.outbound.OutboundFixMessage;

import com.lmax.disruptor.AbstractEvent;

public final class OutboundFixMessageEvent extends AbstractEvent {

    private OutboundFixMessage message;

    public OutboundFixMessageEvent() {}

    public OutboundFixMessage getFixMessage() {
        return this.message;
    }

    public void setFixMessage(final OutboundFixMessage message) {
        this.message = message;
    }
}
