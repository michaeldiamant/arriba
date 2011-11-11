package arriba.examples.handlers;

import java.io.IOException;
import java.util.List;

import org.jboss.netty.channel.Channel;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.Logon;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.transport.TransportRepository;

public final class AuthenticatingLogonHandler implements Handler<Logon> {

    private final String expectedUsername;
    private final String expectedPassword;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final List<Channel> channels;
    private final TransportRepository<String, ?> transportRepository;
    private final RichOutboundFixMessageBuilder builder;

    public AuthenticatingLogonHandler(final String expectedUsername,
            final String expectedPassword,
            final Sender<OutboundFixMessage> fixMessageSender,
            final RichOutboundFixMessageBuilder builder,
            final List<Channel> channels,
            final TransportRepository<String, ?> transportRepository) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
        this.fixMessageSender = fixMessageSender;
        this.builder = builder;
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

        this.builder
        .addStandardHeader(MessageType.LOGON, message)

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
