package arriba.disruptor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import arriba.fix.FixMessageBuilder;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.inbound.InboundFixMessage;
import arriba.transport.netty.util.FixMessages;
import arriba.utils.Field;

import com.lmax.disruptor.EventHandler;


public class DeserializingFixMessageEventHandlerTest {

    private EventHandler<FixMessageEvent> handler;

    @Before
    public void before() {
        final FixMessageBuilder fixMessageBuilder =
                new FixMessageBuilder(mock(FixChunkBuilder.class), mock(FixChunkBuilder.class),
                        mock(FixChunkBuilder.class));
        this.handler =
                new DeserializingFixMessageEventHandler(fixMessageBuilder);
    }

    @Test
    public void verifyFixMessageIsSet() throws Exception {
        final FixMessageEvent fixMessageEntry = createPreloadedFixMessageEntry();

        this.handler.onEvent(fixMessageEntry, false);

        assertNotNull(fixMessageEntry.getFixMessage());
    }

    @Ignore
    @Test
    public void testFixMessageDeserialization() throws Exception {
        final FixMessageEvent fixMessageEntry = createPreloadedFixMessageEntry();

        this.handler.onEvent(fixMessageEntry, false);

        assertAllFieldsAreSet(fixMessageEntry.getFixMessage());
    }

    private static void assertAllFieldsAreSet(final InboundFixMessage inboundFixMessage) {
        for (final Field<String> field : FixMessages.toFields(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
            final String value = inboundFixMessage.getValue(field.getTag());

            assertThat("Tag " + field.getTag() + " is missing a value.", value, is(not("")));
        }
    }

    private static FixMessageEvent createPreloadedFixMessageEntry() {
        final FixMessageEvent fixMessageEntry = new FixMessageEvent();
        fixMessageEntry.setSerializedFixMessage(FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE));

        return fixMessageEntry;
    }
}
