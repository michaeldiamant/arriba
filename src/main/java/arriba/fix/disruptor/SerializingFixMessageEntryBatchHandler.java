package arriba.fix.disruptor;

import org.jboss.netty.buffer.ChannelBuffers;

import arriba.fix.messages.FixMessage;

import com.lmax.disruptor.BatchHandler;

public final class SerializingFixMessageEntryBatchHandler implements BatchHandler<FixMessageEntry> {
	
	public SerializingFixMessageEntryBatchHandler() {}
	
	@Override
	public void onAvailable(final FixMessageEntry entry) throws Exception {
		final FixMessage fixMessage = entry.getFixMessage();
		
		entry.setSerializedFixMessage(ChannelBuffers.copiedBuffer(fixMessage.toByteArray()));
	}
	
	@Override
	public void onEndOfBatch() throws Exception {}
}
