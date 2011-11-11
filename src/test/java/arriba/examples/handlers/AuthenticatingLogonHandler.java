package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.channel.Channel;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.BeginString;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.Logon;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RawOutboundFixMessageBuilder;
import arriba.transport.TransportRepository;

public final class AuthenticatingLogonHandler implements Handler<Logon> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final String expectedUsername;
    private final String expectedPassword;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final AtomicInteger messageCount;
    private final List<Channel> channels;
    private final TransportRepository<String, ?> transportRepository;
    private final RawOutboundFixMessageBuilder builder = new RawOutboundFixMessageBuilder();

    public AuthenticatingLogonHandler(final String expectedUsername, final String expectedPassword,
            final Sender<OutboundFixMessage> fixMessageSender,
            final AtomicInteger messageCount, final List<Channel> channels,
            final TransportRepository<String, ?> transportRepository) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
        this.fixMessageSender = fixMessageSender;
        this.messageCount = messageCount;
        this.channels = channels;
        this.transportRepository = transportRepository;
    }

    @Override
    public void handle(final Logon message) {
        System.out.println("Got logon from " + message.getUsername());

        if (!this.expectedUsername.equals(message.getUsername()) || !this.expectedPassword.equals(message.getPassword())) {
            System.out.println("Username and password do not match!");

            return;
        }

        final SimpleDateFormat sdf = new SimpleDateFormat(SENDING_TIME_FORMAT);

        this.builder
        .addField(Tags.BEGIN_STRING, BeginString.FIXT11.getValue())
        .addField(Tags.MESSAGE_SEQUENCE_NUMBER, String.valueOf(this.messageCount.incrementAndGet()))
        .addField(Tags.MESSAGE_TYPE, MessageType.LOGON.getValue())
        .addField(Tags.SENDER_COMP_ID, message.getTargetCompId())
        .addField(Tags.TARGET_COMP_ID, message.getSenderCompId())
        .addField(Tags.SENDING_TIME, sdf.format(new Date()))

        .addField(Tags.USERNAME, message.getUsername())
        .addField(Tags.PASSWORD, message.getPassword());

        try {
            // TODO Need to figure out right way to negotiate channel registration server-side.
            // Assuming first channel entry is the 'right' one.

            //            final Channel channelToAdd = this.channels.remove(0);
            //            this.transportRepository.add(message.getSenderCompId(), channelToAdd);

            this.fixMessageSender.send(this.builder.build());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
