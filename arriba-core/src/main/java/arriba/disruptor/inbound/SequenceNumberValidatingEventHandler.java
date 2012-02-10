package arriba.disruptor.inbound;

import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.messages.InboundFixMessage;
import arriba.fix.inbound.messages.SequenceReset;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SequenceNumberValidatingEventHandler implements EventHandler<InboundEvent> {

    private final SessionResolver resolver;
    private final RichOutboundFixMessageBuilder builder;
    private final InboundFixMessage[] validatedMessages = new InboundFixMessage[100]; // TODO Set reasonable default.
    private final OutboundFixMessage[] outboundMessages = new OutboundFixMessage[100];

    private int validatedMessagesIndex = 0;

    public SequenceNumberValidatingEventHandler(final SessionResolver resolver, final RichOutboundFixMessageBuilder builder) {
        this.resolver = resolver;
        this.builder = builder;
    }

    @Override
    public void onEvent(final InboundEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        final InboundFixMessage[] messages = event.getMessages();
        for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
            final InboundFixMessage message = messages[messageIndex];
            final Session session = this.resolver.resolve(message.getSessionId());
            if (null == session) {
                throw new IllegalArgumentException("No session found for " + message.getSessionId());
            }

            if (shouldForwardMessage(session, message)) {
                this.processMsg(session, message);
            }

            if (session.isAwaitingResend() && session.isResendComplete()) {
                this.processQueuedMessages(session);
            }
        }

        this.setEventMessages(event);
        this.validatedMessagesIndex = 0;
    }

    // TODO Consider a recursive message processing algorithm that uses LinkedList instead of Array of messages.
    // Queued messages can be prepended to the list of messages to remove nested duplication.

    private void processMsg(final Session session, final InboundFixMessage message) {
        final int sequenceNumber = getSequenceNumber(message);
        final int compareResult = session.compareToInboundSequenceNumber(sequenceNumber);
        if (0 == compareResult) {
            updateSessionState(session, message);

            this.validatedMessages[this.validatedMessagesIndex] = message;
        } else if (compareResult < 0) {
            final OutboundFixMessage logout = this.builder
                    .addStandardHeader(MessageType.LOGOUT, message)
                    .addField(Tags.TEXT,
                            "Expected sequence number " + session.getExpectedInboundSequenceNumber() + ", but received " + sequenceNumber + ".")
                            .build();
            this.outboundMessages[this.validatedMessagesIndex] = logout;
        } else {
            session.queueMessage(message);
            if (!session.isAwaitingResend()) {
                final OutboundFixMessage resendRequest = this.builder.addStandardHeader(MessageType.RESEND_REQUEST, message)
                        .addField(Tags.BEGIN_SEQUENCE_NUMBER, Integer.toString(session.getExpectedInboundSequenceNumber()))
                        .addField(Tags.END_SEQUENCE_NUMBER, Integer.toString(getSequenceNumber(message) - 1))
                        .build();

                this.outboundMessages[this.validatedMessagesIndex] = resendRequest;
            }
        }
        ++this.validatedMessagesIndex;
    }

    private void setEventMessages(final InboundEvent event) {
        // TODO Skip copy if original messages == validated messages.
        final InboundFixMessage[] validatedMessagesCopy = new InboundFixMessage[this.validatedMessagesIndex];
        System.arraycopy(this.validatedMessages, 0, validatedMessagesCopy, 0, validatedMessagesCopy.length);
        event.setMessages(validatedMessagesCopy);

        final OutboundFixMessage[] outboundMessagesCopy = new OutboundFixMessage[this.validatedMessagesIndex];
        System.arraycopy(this.outboundMessages, 0, outboundMessagesCopy, 0, outboundMessagesCopy.length);
        event.setOutboundMessages(outboundMessagesCopy);
    }

    private static void updateSessionState(final Session session, final InboundFixMessage message) {
        if (message.isA(MessageType.SEQUENCE_RESET)) {
            processSequenceReset(session, (SequenceReset) message);
        } else {
            session.incrementInboundSequenceNumber();
        }
        session.updateLastReceivedTimestamp();
    }

    private void processQueuedMessages(final Session session) {
        // TODO Refactor (maybe recursively)
        boolean isValid = true;
        while (isValid) {
            final InboundFixMessage queuedMessage = session.peek();
            if (queuedMessage == null) {
                isValid = false;
            } else {
                isValid = shouldForwardMessage(session, queuedMessage);
                if (isValid) {
                    this.processMsg(session, queuedMessage);
                }
                session.dropHead();
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

    private static int getSequenceNumber(final InboundFixMessage message) {
        return Integer.parseInt(message.getHeaderValue(Tags.MESSAGE_SEQUENCE_NUMBER));
    }

    private static boolean shouldForwardMessage(final Session session, final InboundFixMessage message) {
        // FIXME Support SequenceReset Reset messages.
        // FIXME Support proper semantics for mismatch on response by message type (p. 13 FIXT1.1 spec).
        if (!session.isAwaitingResend() &&
                message.hasHeaderValue(Tags.POSSIBLE_DUPLICATE_FLAG) &&
                "Y".equalsIgnoreCase(message.getHeaderValue(Tags.POSSIBLE_DUPLICATE_FLAG))) {
            return false;
        }

        return true;
    }
}
