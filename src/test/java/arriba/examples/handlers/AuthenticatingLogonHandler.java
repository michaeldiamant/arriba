package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.FixMessageBuilder;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.Logon;
import arriba.transport.channels.ChannelRepository;

public final class AuthenticatingLogonHandler implements Handler<Logon> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final String expectedUsername;
    private final String expectedPassword;
    private final FixMessageBuilder fixMessageBuilder;
    private final Sender<InboundFixMessage> fixMessageSender;
    private final AtomicInteger messageCount;
    private final List<Channel> channels;
    private final ChannelRepository<String> channelRepository;

    public AuthenticatingLogonHandler(final String expectedUsername, final String expectedPassword,
            final FixMessageBuilder fixMessageBuilder, final Sender<InboundFixMessage> fixMessageSender,
            final AtomicInteger messageCount, final List<Channel> channels,
            final ChannelRepository<String> channelRepository) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
        this.fixMessageBuilder = fixMessageBuilder;
        this.fixMessageSender = fixMessageSender;
        this.messageCount = messageCount;
        this.channels = channels;
        this.channelRepository = channelRepository;
    }

    @Override
    public void handle(final Logon message) {
        System.out.println("Got logon from " + message.getUsername());

        if (!this.expectedUsername.equals(message.getUsername()) || !this.expectedPassword.equals(message.getPassword())) {
            System.out.println("Username and password do not match!");

            return;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.fixMessageBuilder.addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.incrementAndGet()));
        this.fixMessageBuilder.setMessageType("A");
        this.fixMessageBuilder.setBeginStringBytes(BeginString.FIXT11);
        this.fixMessageBuilder.addField(Tags.SENDER_COMP_ID, message.getTargetCompId());
        this.fixMessageBuilder.addField(Tags.TARGET_COMP_ID, message.getSenderCompId());
        this.fixMessageBuilder.addField(Tags.SENDING_TIME, sdf.format(new Date()));

        this.fixMessageBuilder.addField(Tags.USERNAME, message.getUsername());
        this.fixMessageBuilder.addField(Tags.PASSWORD, message.getPassword());

        try {
            // TODO Need to figure out right way to negotiate channel registration server-side.
            // Assuming first channel entry is the 'right' one.
            final Channel channelToAdd = this.channels.remove(0);
            this.channelRepository.add(message.getSenderCompId(), channelToAdd);

            this.fixMessageSender.send(this.fixMessageBuilder.build());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        this.fixMessageBuilder.clear();
    }

}
