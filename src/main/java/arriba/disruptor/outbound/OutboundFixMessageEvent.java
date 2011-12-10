package arriba.disruptor.outbound;

import arriba.fix.outbound.OutboundFixMessage;

import com.lmax.disruptor.AbstractEvent;

public final class OutboundFixMessageEvent extends AbstractEvent {

    private OutboundFixMessage message;
    private String targetCompId;

    public OutboundFixMessageEvent() {}

    public OutboundFixMessage getFixMessage() {
        return this.message;
    }

    public void setFixMessage(final OutboundFixMessage message) {
        this.message = message;
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }

    public void setTargetCompId(final String targetCompId) {
        this.targetCompId = targetCompId;
    }
}
