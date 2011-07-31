package arriba.fix.disruptor;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.messages.FixMessage;

import com.lmax.disruptor.AbstractEntry;

public final class FixMessageEntry extends AbstractEntry  {

    private ChannelBuffer serializedFixMessage;
    private FixMessage fixMessage;

    public FixMessageEntry() {}

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
