package arriba.fix.netty;

import org.jboss.netty.channel.Channel;

import arriba.common.Sender;
import arriba.fix.messages.FixMessage;

public final class FixMessageChannelSender implements Sender<FixMessage> {

    private final ChannelRepository<String> channelRepository;

    public FixMessageChannelSender(final ChannelRepository<String> channelRepository) {
        this.channelRepository = channelRepository;
    }

    public void send(final FixMessage message) {
        final Channel channel = this.channelRepository.find(message.getSenderCompId());
        if (null != channel) {
            channel.write(message)
        } else {
            // TODO Log error
            System.out.println("received unknown channel ID: " + message.getSenderCompId());
        }
    }
}
