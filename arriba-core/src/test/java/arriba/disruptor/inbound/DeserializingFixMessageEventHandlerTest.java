package arriba.disruptor.inbound;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import arriba.fix.chunk.FixChunkBuilderSupplier;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.InboundFixMessageFactory;
import arriba.fix.inbound.RepeatingGroupBuilder;
import arriba.transport.netty.util.FixMessages;
import arriba.utils.Field;

import com.lmax.disruptor.EventHandler;


public class DeserializingFixMessageEventHandlerTest {

    private EventHandler<InboundEvent> handler;

    @Before
    public void before() {
        final InboundFixMessageBuilder inboundFixMessageBuilder = new InboundFixMessageBuilder(
                mock(FixChunkBuilderSupplier.class),
                new InboundFixMessageFactory()
                );
        final RepeatingGroupBuilder repeatingGroupBuilder =
                new RepeatingGroupBuilder(mock(FixChunkBuilderSupplier.class));

        this.handler =
                new DeserializingFixMessageEventHandler(inboundFixMessageBuilder, repeatingGroupBuilder);
    }

    @Ignore
    @Test
    public void testFixMessageDeserialization() throws Exception {
        final InboundEvent event = createPreloadedFixMessageEvent();

        this.handler.onEvent(event, 1, false);

        for (final InboundFixMessage message : event.getMessages()) {
            assertAllFieldsAreSet(message);
        }
    }

    private static void assertAllFieldsAreSet(final InboundFixMessage inboundFixMessage) {
        for (final Field<String> field : FixMessages.toFields(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
            final String value = inboundFixMessage.getValue(field.getTag());

            assertThat("Tag " + field.getTag() + " is missing a value.", value, is(not("")));
        }
    }

    private static InboundEvent createPreloadedFixMessageEvent() {
        final InboundEvent fixMessageEntry = new InboundEvent();
        fixMessageEntry.setSerializedMessages(
                new ChannelBuffer[] {
                        FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)
                });

        return fixMessageEntry;
    }
}
