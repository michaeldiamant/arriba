package arriba.fix.disruptor;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Before;
import org.junit.Test;

import arriba.fix.Field;
import arriba.fix.FixFieldCollection;
import arriba.fix.FixFieldCollection.Builder;
import arriba.fix.Tags;
import arriba.fix.messages.FixMessageFactory;
import arriba.fix.netty.util.FixMessages;

import com.lmax.disruptor.BatchHandler;

public class SerializingFixMessageEntryBatchHandlerTest {
	
	private BatchHandler<FixMessageEntry> handler;
	
	@Before
	public void before() {
		this.handler = new SerializingFixMessageEntryBatchHandler();
	}
	
	@Test
	public void verifySerializedFixMessageIsSet() throws Exception {
		final FixMessageEntry fixMessageEntry = createPreloadedFixMessageEntry();
		
		this.handler.onAvailable(fixMessageEntry);
		
		assertNotNull(fixMessageEntry.getSerializedFixMessage());
	}
	
	@Test
	public void testFixMessageSerialization() throws Exception {
		final FixMessageEntry fixMessageEntry = createPreloadedFixMessageEntry();
		
		this.handler.onAvailable(fixMessageEntry);
		
		final ChannelBuffer expectedSerializedFixMessage = FixMessages.toChannelBuffer(FixMessages.EXAMPLE_NEW_ORDER_SINGLE);
		assertThatByteArraysAreEqual(fixMessageEntry.getSerializedFixMessage(), expectedSerializedFixMessage);
	}
	
	private static void assertThatByteArraysAreEqual(final ChannelBuffer actualBuffer, final ChannelBuffer expectedBuffer) {
		assertThat(actualBuffer.array(), is(expectedBuffer.array()));
	}
	
	private static FixMessageEntry createPreloadedFixMessageEntry() {
		final FixMessageEntry fixMessageEntry = new FixMessageEntry();
		
		final Builder fixFieldCollectionBuilder = new FixFieldCollection.Builder();
		String messageType = "";
		for (final Field<String> field : FixMessages.toFields(FixMessages.EXAMPLE_NEW_ORDER_SINGLE)) {
			fixFieldCollectionBuilder.addField(field.getTag(), field.getValue());
			
			if (Tags.MESSAGE_TYPE == field.getTag()) {
				messageType = field.getValue();
			}
		}
		
		fixMessageEntry.setFixMessage(FixMessageFactory.create(fixFieldCollectionBuilder.build(), messageType));
		
		return fixMessageEntry;
	}
}
