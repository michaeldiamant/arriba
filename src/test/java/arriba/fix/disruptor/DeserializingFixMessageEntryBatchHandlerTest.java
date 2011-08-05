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
