package arriba.disruptor.inbound;

import arriba.fix.inbound.InboundFixMessage;

import com.lmax.disruptor.AbstractEvent;
import org.jboss.netty.buffer.ChannelBuffer;

public final class InboundFixMessageEvent extends AbstractEvent {

    private ChannelBuffer serializedFixMessage;
    private InboundFixMessage inboundFixMessage;

    public InboundFixMessageEvent() {}

    public void setFixMessage(final InboundFixMessage inboundFixMessage) {
        this.inboundFixMessage = inboundFixMessage;
    }

    public InboundFixMessage getFixMessage() {
        return this.inboundFixMessage;
    }

    public ChannelBuffer getSerializedFixMessage() {
        return this.serializedFixMessage;
    }

    public void setSerializedFixMessage(final ChannelBuffer serializedFixMessage) {
        this.serializedFixMessage = serializedFixMessage;
    }
}
