package arriba.disruptor.inbound;

import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.SequenceReset;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SequenceNumberValidatingEventHandler implements EventHandler<InboundEvent> {

    private static final int EXPECTED_SEQUENCE_NUMBER = 0;

    private final SessionResolver resolver;
    private final Sender<OutboundFixMessage> sender;
    private final RichOutboundFixMessageBuilder builder;
    private final InboundFixMessage[] validatedMessages = new InboundFixMessage[2000]; // TODO Set reasonable default.

    private int validatedMessagesIndex = 0;

    public SequenceNumberValidatingEventHandler(final SessionResolver resolver, final Sender<OutboundFixMessage> sender,
            final RichOutboundFixMessageBuilder builder) {
        this.resolver = resolver;
        this.sender = sender;
        this.builder = builder;
    }

    @Override
    public void onEvent(final InboundEvent event, final boolean endOfBatch) throws Exception {
        final InboundFixMessage[] messages = event.getMessages();
        for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
            final InboundFixMessage message = messages[messageIndex];
            final Session session = this.resolver.resolve(message.getSessionId());
            if (null == session) {
                throw new IllegalArgumentException("No session found for " + message.getSessionId());
            }

            if (this.shouldForwardMessage(session, message)) {
                this.validatedMessages[this.validatedMessagesIndex++] = message;

                if (session.isAwaitingResend() && session.isResendComplete()) {
                    this.processQueuedMessages(session);
                }
            }
        }

        this.setEventMessages(event);
    }

    private void setEventMessages(final InboundEvent event) {
        // TODO Skip copy if original messages == validated messages.
        final InboundFixMessage[] validatedMessagesCopy = new InboundFixMessage[this.validatedMessagesIndex];
        System.arraycopy(this.validatedMessages, 0, validatedMessagesCopy, 0, validatedMessagesCopy.length);
        event.setMessages(validatedMessagesCopy);
    }

    private void processQueuedMessages(final Session session) {
        // TODO Refactor (maybe recursively)
        boolean isValid = true;
        while (isValid) {
            final InboundFixMessage queuedMessage = session.peek();
            if (queuedMessage == null) {
                isValid = false;
            } else {
                isValid = this.shouldForwardMessage(session, queuedMessage);
                if (isValid) {
                    this.validatedMessages[this.validatedMessagesIndex++] = queuedMessage;
                    session.dropHead();
                }
            }
        }
    }

    private static void processSequenceReset(final Session session, final SequenceReset sequenceReset) {
        if ("Y".equalsIgnoreCase(sequenceReset.getHeaderValue(Tags.POSSIBLE_DUPLICATE_FLAG))) {
            if ("Y".equalsIgnoreCase(sequenceReset.getGapFillFlag())) {
                final int newSequenceNumber = Integer.parseInt(sequenceReset.getNewSequenceNumber());
                // FIXME Assuming new sequence number is not larger than first queued message.
                session.setInboundSequenceNumber(newSequenceNumber);
            } else {
                // TODO What should happen?
            }
        } else if ("N".equalsIgnoreCase(sequenceReset.getGapFillFlag())) {
            // TODO Handle SequenceReset Reset messages.
        }
    }

    // FIXME Refactor to remove side-effects from this method.
    private boolean shouldForwardMessage(final Session session, final InboundFixMessage message) {
        // FIXME Support SequenceReset Reset messages.
        // FIXME Support proper semantics for mismatch on response by message type (p. 13 FIXT1.1 spec).

        if (!session.isAwaitingResend() && "Y".equalsIgnoreCase(message.getHeaderValue(Tags.POSSIBLE_DUPLICATE_FLAG))) {
            return false;
        }

        final int sequenceNumber = Integer.parseInt(message.getHeaderValue(Tags.MESSAGE_SEQUENCE_NUMBER));
        final int compareResult = session.compareToInboundSequenceNumber(sequenceNumber);
        if (EXPECTED_SEQUENCE_NUMBER == compareResult) {
            if (message.isA(MessageType.SEQUENCE_RESET)) {
                processSequenceReset(session, (SequenceReset) message);
            } else {
                session.incrementInboundSequenceNumber();
            }
            session.updateLastReceivedTimestamp();

            return true;
        } else if (compareResult < EXPECTED_SEQUENCE_NUMBER) {
            final OutboundFixMessage logout = this.builder
                    .addStandardHeader(MessageType.LOGOUT, message)
                    .addField(Tags.TEXT,
                            "Expected sequence number " + session.getExpectedInboundSequenceNumber() + ", but received " + sequenceNumber + ".")
                            .build();
            this.sender.send(logout);

            return false;
        } else {
            if (!session.isAwaitingResend()) {
                final OutboundFixMessage resendRequest = this.builder.addStandardHeader(MessageType.RESEND_REQUEST, message)
                        .addField(Tags.BEGIN_SEQUENCE_NUMBER, Integer.toString(session.getExpectedInboundSequenceNumber()))
                        .addField(Tags.END_SEQUENCE_NUMBER, Integer.toString(sequenceNumber - 1)).build();
                this.sender.send(resendRequest);
            }
            session.queueMessage(message);

            return false;
        }
    }
}
