package arriba.fix.netty;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import arriba.common.Sender;
import arriba.fix.messages.FixMessage;

public final class FixMessageChannelSender implements Sender<FixMessage> {

    private final ChannelRepository<String> channelRepository;

    public FixMessageChannelSender(final ChannelRepository<String> channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void send(final FixMessage message) throws IOException {
        final Channel channel;
        try {
            channel = this.channelRepository.find(message.getSenderCompId());
        } catch (final UnknownChannelIdException e) {
            throw new IOException(e);
        }

        final ChannelBuffer messageBuffer = ChannelBuffers.copiedBuffer(message.toByteArray());
        channel.write(messageBuffer);
    }
}
