package arriba.examples.handlers;

import java.util.List;

import org.jboss.netty.channel.Channel;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.Logon;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.SessionId;
import arriba.fix.session.SessionMonitor;
import arriba.transport.TransportIdentity;
import arriba.transport.TransportRepository;

public final class AuthenticatingLogonHandler implements Handler<Logon> {

    // FIXME Transport repository should not explicitly reference Channel.

    private final String expectedUsername;
    private final String expectedPassword;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final List<Channel> channels;
    private final TransportRepository<String, Channel> transportRepository;
    private final RichOutboundFixMessageBuilder builder;
    private final SessionMonitor monitor;

    public AuthenticatingLogonHandler(final String expectedUsername,
            final String expectedPassword,
            final Sender<OutboundFixMessage> fixMessageSender,
            final RichOutboundFixMessageBuilder builder,
            final List<Channel> channels,
            final TransportRepository<String, Channel> transportRepository,
            final SessionMonitor monitor) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
        this.fixMessageSender = fixMessageSender;
        this.builder = builder;
        this.channels = channels;
        this.transportRepository = transportRepository;
        this.monitor = monitor;
    }

    @Override
    public void handle(final Logon message) {
        System.out.println("Got logon from " + message.getUsername());

        if (!this.expectedUsername.equals(message.getUsername()) || !this.expectedPassword.equals(message.getPassword())) {
            System.out.println("Username and password do not match!");

            return;
        }

        this.builder
        .addStandardHeader(MessageType.LOGON, message)

        .addField(Tags.USERNAME, message.getUsername())
        .addField(Tags.PASSWORD, message.getPassword())
        .addField(Tags.HEARTBEAT_INTERVAL, message.getHeartbeatInterval());

        if (message.hasBodyValue(Tags.RESET_SEQUENCE_NUMBER_FLAG)) {
            this.builder.addField(Tags.RESET_SEQUENCE_NUMBER_FLAG, message.getResetSequenceNumberFlag());
        }

        // TODO Need to figure out right way to negotiate channel registration server-side.
        // Assuming first channel entry is the 'right' one.

        final Channel channelToAdd = this.channels.remove(0);
        this.transportRepository.add(message.getSenderCompId(), new TransportIdentity<>(channelToAdd));

        this.monitor.monitor(
                new SessionId(message.getTargetCompId(), message.getSenderCompId()),
                Integer.parseInt(message.getHeartbeatInterval()) * 1000
                );

        this.fixMessageSender.send(this.builder.build());
    }

}
