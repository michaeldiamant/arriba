package arriba.disruptor;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import arriba.fix.messages.FixMessage;
import arriba.transport.channels.ChannelRepository;
import arriba.transport.channels.UnknownChannelIdException;

import com.lmax.disruptor.EventHandler;


public final class ChannelWritingFixMessageEventHandler implements EventHandler<FixMessageEvent> {

    private final ChannelRepository<String> channelRepository;

    public ChannelWritingFixMessageEventHandler(final ChannelRepository<String> channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public void onEvent(final FixMessageEvent entry, final boolean b) throws Exception {
        final FixMessage fixMessage = entry.getFixMessage();

        final Channel channel;
        try {
            channel = this.channelRepository.find(fixMessage.getTargetCompId());
        } catch (final UnknownChannelIdException e) {
            throw new IOException(e);
        }

        final ChannelBuffer messageBuffer = ChannelBuffers.copiedBuffer(fixMessage.toByteArray());
        channel.write(messageBuffer);
    }

    public void onEndOfBatch() throws Exception {}

}
