package arriba.disruptor.inbound;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.inbound.InboundFixMessage;

import com.lmax.disruptor.AbstractEvent;

public final class InboundEvent extends AbstractEvent {

    private ChannelBuffer[] serializedMessages;
    private InboundFixMessage[] messages;

    public InboundEvent() {}

    public ChannelBuffer[] getSerializedMessages() {
        return this.serializedMessages;
    }

    public void setSerializedMessages(final ChannelBuffer[] serializedMessages) {
        this.serializedMessages = serializedMessages;
    }

    public InboundFixMessage[] getMessages() {
        return this.messages;
    }

    public void setMessages(final InboundFixMessage[] messages) {
        this.messages = messages;
    }
}
