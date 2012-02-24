package arriba.disruptor.inbound;

import arriba.transport.TransportIdentity;
import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.inbound.messages.InboundFixMessage;
import arriba.fix.outbound.OutboundFixMessage;

public final class InboundEvent<T> {

    private ChannelBuffer[] serializedMessages;
    private InboundFixMessage[] messages;
    private OutboundFixMessage[] outboundMessages;
    private TransportIdentity<T> identity;

    public InboundEvent() {}

    public TransportIdentity<T> getIdentity() {
        return identity;
    }

    public void setIdentity(TransportIdentity<T> identity) {
        this.identity = identity;
    }

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
