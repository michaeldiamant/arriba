package arriba.disruptor.inbound;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.outbound.OutboundFixMessage;

public final class InboundEvent {

    private ChannelBuffer[] serializedMessages;
    private InboundFixMessage[] messages;
    private OutboundFixMessage[] outboundMessages;

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

    public OutboundFixMessage[] getOutboundMessages() {
        return this.outboundMessages;
    }

    public void setOutboundMessages(final OutboundFixMessage[] outboundMessages) {
        this.outboundMessages = outboundMessages;
    }

    public void reset() {
        this.serializedMessages = null;
        this.messages = null;
        this.outboundMessages = null;
    }
}
