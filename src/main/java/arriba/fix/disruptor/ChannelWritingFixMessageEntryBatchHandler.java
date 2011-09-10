package arriba.fix.disruptor;

import java.io.IOException;

import com.lmax.disruptor.EventHandler;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import arriba.fix.messages.FixMessage;
import arriba.fix.netty.ChannelRepository;
import arriba.fix.netty.UnknownChannelIdException;


public final class ChannelWritingFixMessageEntryBatchHandler implements EventHandler<FixMessageEntry> {

    private final ChannelRepository<String> channelRepository;

    public ChannelWritingFixMessageEntryBatchHandler(final ChannelRepository<String> channelRepository) {
        this.channelRepository = channelRepository;
    }

  @Override
  public void onEvent(final FixMessageEntry entry, boolean b) throws Exception {
        final FixMessage fixMessage = entry.getFixMessage();

        final Channel channel;
        try {
            channel = this.channelRepository.find(fixMessage.getSenderCompId());
        } catch (final UnknownChannelIdException e) {
            throw new IOException(e);
        }

        final ChannelBuffer messageBuffer = ChannelBuffers.copiedBuffer(fixMessage.toByteArray());
        channel.write(messageBuffer);
    }

    public void onEndOfBatch() throws Exception {}

}
