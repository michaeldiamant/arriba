package arriba.fix.disruptor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import arriba.fix.Field;
import arriba.fix.messages.FixMessage;
import arriba.fix.netty.util.FixMessages;

import com.lmax.disruptor.BatchHandler;

public class DeserializingFixMessageEntryBatchHandlerTest {

    private static final String EXAMPLE_FIX_MESSAGE = "8=FIX.4.0\u00019=86\u000135=D\u000149=0\u000156=0\u000134=1\u000152=99990909-17:17:17"
        + "\u000111=90001008\u000121=1\u000155=IBM\u000154=1\u000138=10\u000140=1\u000159=0\u000110=191\u0001";

    private BatchHandler<FixMessageEntry> handler;

    @Before
    public void before() {
        this.handler =  new DeserializingFixMessageEntryBatchHandler();
    }

    @Test
    public void verifyFixMessageIsSet() throws Exception {
        final FixMessageEntry fixMessageEntry = createPreloadedFixMessageEntry();

        this.handler.onAvailable(fixMessageEntry);

        assertNotNull(fixMessageEntry.getFixMessage());
    }

    @Test
    public void testFixMessageDeserialization() throws Exception {
        final FixMessageEntry fixMessageEntry = createPreloadedFixMessageEntry();

        this.handler.onAvailable(fixMessageEntry);

        assertAllFieldsAreSet(fixMessageEntry.getFixMessage());
    }

    private static void assertAllFieldsAreSet(final FixMessage fixMessage) {
        for (final Field<String> field : FixMessages.toFields(EXAMPLE_FIX_MESSAGE)) {
            final String value = fixMessage.getValue(field.getTag());

            assertThat("Tag " + field.getTag() + " is missing a value.", value, is(not("")));
        }
    }

    private static FixMessageEntry createPreloadedFixMessageEntry() {
        final FixMessageEntry fixMessageEntry = new FixMessageEntry();
        fixMessageEntry.setSerializedFixMessage(FixMessages.toChannelBuffer(EXAMPLE_FIX_MESSAGE));

        return fixMessageEntry;
    }
}
