package arriba.disruptor.inbound;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.RepeatingGroupBuilder;
import arriba.fix.inbound.deserializers.InboundFixMessageDeserializer;

import com.lmax.disruptor.EventHandler;

public final class DeserializingFixMessageEventHandler implements EventHandler<InboundEvent> {

    private final InboundFixMessageDeserializer deserializer;

    public DeserializingFixMessageEventHandler(final InboundFixMessageBuilder inboundFixMessageBuilder,
            final RepeatingGroupBuilder repeatingGroupBuilder) {
        this.deserializer = new InboundFixMessageDeserializer(inboundFixMessageBuilder, repeatingGroupBuilder);
    }

    @Override
    public void onEvent(final InboundEvent event, final boolean endOfBatch) throws Exception {
        final ChannelBuffer[] serializedMessages = event.getSerializedMessages();
        final InboundFixMessage[] messages = new InboundFixMessage[serializedMessages.length];

        for (int messageIndex = 0; messageIndex < serializedMessages.length; messageIndex++) {
            final InboundFixMessage message = this.deserializer.deserialize(serializedMessages[messageIndex]);
            if (null != message) {
                messages[messageIndex] = message;
            }
        }

        event.setMessages(messages);
    }

}
