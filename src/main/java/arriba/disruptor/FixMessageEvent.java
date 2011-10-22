package arriba.disruptor;

import arriba.fix.inbound.FixMessage;

import com.lmax.disruptor.AbstractEvent;
import org.jboss.netty.buffer.ChannelBuffer;

public final class FixMessageEvent extends AbstractEvent {

    private ChannelBuffer serializedFixMessage;
    private FixMessage fixMessage;

    public FixMessageEvent() {}

    public void setFixMessage(final FixMessage fixMessage) {
        this.fixMessage = fixMessage;
    }

    public FixMessage getFixMessage() {
        return this.fixMessage;
    }

    public ChannelBuffer getSerializedFixMessage() {
        return this.serializedFixMessage;
    }

    public void setSerializedFixMessage(final ChannelBuffer serializedFixMessage) {
        this.serializedFixMessage = serializedFixMessage;
    }
}
