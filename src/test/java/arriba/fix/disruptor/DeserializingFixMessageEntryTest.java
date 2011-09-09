package arriba.fix.disruptor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import arriba.fix.Field;
import arriba.fix.FixMessageBuilder;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.messages.FixMessage;
import arriba.fix.netty.util.FixMessages;

import com.lmax.disruptor.BatchHandler;

public class DeserializingFixMessageEntryTest {

    private BatchHandler<FixMessageEntry> handler;

    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        final FixMessageBuilder<FixChunk> fixMessageBuilder =
            new FixMessageBuilder<FixChunk>(mock(FixChunkBuilder.class), mock(FixChunkBuilder.class),
                    mock(FixChunkBuilder.class));
        this.handler =
            new DeserializingFixMessageEntry(fixMessageBuilder);
    }

    @Test
    public void verifyFixMessageIsSet() throws Exception {
        final FixMessageEntry fixMessageEntry = createPreloadedFixMessageEntry();

        this.handler.onAvailable(fixMessageEntry);

        assertNotNull(fixMessageEntry.getFixMessage());
    }

    @Ignore
    @Test
    public void testFixMessageDeserialization() throws Exception {
        final FixMessageEntry fixMessageEntry = createPreloadedFixMessageEntry();

        this.handler.onAvailable(fixMessageEntry);

        assertAllFieldsAreSet(fixMessageEntry.getFixMessage());
    }

    private static void assertAllFieldsAreSet(final FixMessage fixMessage) {
        for (final Field<String> field : FixMessages.toFields(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
            final String value = fixMessage.getValue(field.getTag());

            assertThat("Tag " + field.getTag() + " is missing a value.", value, is(not("")));
        }
    }

    private static FixMessageEntry createPreloadedFixMessageEntry() {
        final FixMessageEntry fixMessageEntry = new FixMessageEntry();
        fixMessageEntry.setSerializedFixMessage(FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE));

        return fixMessageEntry;
    }
}
