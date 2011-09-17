package arriba.examples.handlers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.FixMessageBuilder;
import arriba.fix.Tags;
import arriba.fix.chunk.arrays.ArrayFixChunk;
import arriba.fix.fields.BeginString;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.Logon;

public final class AuthenticatingLogonHandler implements Handler<Logon> {

    private static final String SENDING_TIME_FORMAT = "yyyyMMdd-HH:mm:ss";

    private final String expectedUsername;
    private final String expectedPassword;
    private final FixMessageBuilder<ArrayFixChunk> fixMessageBuilder;
    private final Sender<FixMessage> fixMessageSender;
    private final AtomicInteger messageCount;

    public AuthenticatingLogonHandler(final String expectedUsername, final String expectedPassword,
            final FixMessageBuilder<ArrayFixChunk> fixMessageBuilder, final Sender<FixMessage> fixMessageSender,
            final AtomicInteger messageCount) {
        this.expectedUsername = expectedUsername;
        this.expectedPassword = expectedPassword;
        this.fixMessageBuilder = fixMessageBuilder;
        this.fixMessageSender = fixMessageSender;
        this.messageCount = messageCount;
    }

    @Override
    public void handle(final Logon message) {
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
            this.fixMessageSender.send(this.fixMessageBuilder.build());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        this.fixMessageBuilder.clear();
    }

}
