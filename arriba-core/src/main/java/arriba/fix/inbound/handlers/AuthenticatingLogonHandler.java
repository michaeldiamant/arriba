package arriba.fix.inbound.handlers;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.messages.Logon;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;

public final class AuthenticatingLogonHandler implements Handler<Logon> {

    private final String expectedUsername;
    private final String expectedPassword;
    private final Sender<OutboundFixMessage> fixMessageSender;
    private final RichOutboundFixMessageBuilder builder;


    public AuthenticatingLogonHandler(final String expectedUsername,
            final String expectedPassword,
            final Sender<OutboundFixMessage> fixMessageSender,
            final RichOutboundFixMessageBuilder builder) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
        this.fixMessageSender = fixMessageSender;
        this.builder = builder;
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

        .addField(Tags.ENCRYPT_METHOD, message.getEncryptMethod())
        .addField(Tags.USERNAME, message.getUsername())
        .addField(Tags.PASSWORD, message.getPassword())
        .addField(Tags.HEARTBEAT_INTERVAL, message.getHeartbeatInterval());

        if (message.hasBodyValue(Tags.RESET_SEQUENCE_NUMBER_FLAG)) {
            this.builder.addField(Tags.RESET_SEQUENCE_NUMBER_FLAG, message.getResetSequenceNumberFlag());
        }

        this.fixMessageSender.send(this.builder.build());
    }
}
