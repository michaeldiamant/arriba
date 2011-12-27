package arriba.disruptor.inbound;

import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.MessageHandlingException;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.lmax.disruptor.EventHandler;

public final class SessionNotifyingInboundFixMessageEventHandler implements EventHandler<InboundEvent> {

    private final SessionResolver sessionResolver;
    private final Sender<OutboundFixMessage> sender;
    private final RichOutboundFixMessageBuilder builder;

    public SessionNotifyingInboundFixMessageEventHandler(final SessionResolver sessionResolver,
            final Sender<OutboundFixMessage> sender,
            final RichOutboundFixMessageBuilder builder) {
        this.sessionResolver = sessionResolver;
        this.sender = sender;
        this.builder = builder;
    }

    @Override
    public void onEvent(final InboundEvent event, final boolean endOfBatch) throws Exception {
        final InboundFixMessage[] messages = event.getMessages();
        for (int messageIndex = 0; messageIndex < messages.length; messageIndex++) {
            this.notifySession(messages[messageIndex]);
        }
    }

    private void notifySession(final InboundFixMessage inboundFixMessage) throws MessageHandlingException {
        final Session session = this.sessionResolver.resolve(inboundFixMessage.getSessionId());
        if (null == session) {
            throw new IllegalArgumentException("No session found for " + inboundFixMessage.getSessionId());
        }

        final int sequenceNumber = Integer.parseInt(inboundFixMessage.getHeaderValue(Tags.MESSAGE_SEQUENCE_NUMBER));
        final int compareResult = session.compareToInboundSequenceNumber(sequenceNumber);
        if (0 == compareResult) {
            session.incrementInboundSequenceNumber();
            session.updateLastReceivedTimestamp();

            if  (session.areMessagesQueued()) {
                for (final InboundFixMessage message : session.drainMessageQueue()) {
                    session.onMessage(message);
                }
            }

            session.onMessage(inboundFixMessage);
        } else if (compareResult < 0) {
            final OutboundFixMessage logout = this.builder
                    .addStandardHeader(MessageType.LOGOUT, inboundFixMessage)
                    .addField(Tags.TEXT, "Expected sequence number " + session.getExpectedInboundSequenceNumber() + ", but received " + sequenceNumber + ".")
                    .build();
            this.sender.send(logout);
        } else {
            if (!session.isAwaitingResend()) {
                final OutboundFixMessage resendRequest = this.builder
                        .addStandardHeader(MessageType.RESEND_REQUEST, inboundFixMessage)
                        .addField(Tags.BEGIN_SEQUENCE_NUMBER, Integer.toString(session.getExpectedInboundSequenceNumber()))
                        .addField(Tags.END_SEQUENCE_NUMBER, Integer.toString(sequenceNumber))
                        .build();
                this.sender.send(resendRequest);
            }
            session.queueMessage(inboundFixMessage);
        }
    }
}
