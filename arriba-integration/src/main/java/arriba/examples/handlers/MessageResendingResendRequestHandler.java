package arriba.examples.handlers;

import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffers;

import arriba.common.Handler;
import arriba.common.Sender;
import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.ResendRequest;
import arriba.fix.inbound.deserializers.InboundFixMessageDeserializer;
import arriba.fix.outbound.DateSupplier;
import arriba.fix.outbound.OutboundFixMessage;
import arriba.fix.outbound.RichOutboundFixMessageBuilder;
import arriba.fix.session.Session;
import arriba.fix.session.SessionResolver;

import com.google.common.collect.Sets;

public final class MessageResendingResendRequestHandler implements Handler<ResendRequest> {

    private static final Set<String> messageTypesNotResent =
            Sets.newHashSet(
                    MessageType.HEARTBEAT.getValue(),
                    MessageType.TEST_REQUEST.getValue(),
                    MessageType.LOGON.getValue(),
                    MessageType.LOGOUT.getValue(),
                    MessageType.RESEND_REQUEST.getValue(),
                    MessageType.SEQUENCE_RESET.getValue()
                    );

    private final SessionResolver resolver;
    private final Sender<byte[]> sender;
    private final RichOutboundFixMessageBuilder builder;
    private final InboundFixMessageDeserializer deserializer;

    public MessageResendingResendRequestHandler(final SessionResolver resolver, final Sender<byte[]> sender,
            final RichOutboundFixMessageBuilder builder, final InboundFixMessageDeserializer deserializer) {
        this.resolver = resolver;
        this.sender = sender;
        this.builder = builder;
        this.deserializer = deserializer;
    }

    @Override
    public void handle(final ResendRequest request) {
        final int startingSequenceNumber = Integer.parseInt(request.getBeginSequenceNumber());
        final int endingSequenceNumber = Integer.parseInt(request.getEndSequenceNumber());
        final Session session = this.resolver.resolve(request.getSessionId());
        final byte[][] serializedMessages = session.retrieve(startingSequenceNumber, endingSequenceNumber);

        for (final byte[] serializedMessage : serializedMessages) {
            final InboundFixMessage inboundMessage = this.deserializer.deserialize(ChannelBuffers.copiedBuffer(serializedMessage));
            final int originalSequenceNumber = Integer.parseInt(inboundMessage.getHeaderValue(Tags.MESSAGE_SEQUENCE_NUMBER));

            final OutboundFixMessage outboundMessage;
            if (messageTypesNotResent.contains(inboundMessage.getMessageType())) {
                // TODO Support GapFill messages with fill size > 1.
                outboundMessage = this.builder
                        .addStandardHeader(MessageType.SEQUENCE_RESET, request)
                        .addField(Tags.POSSIBLE_DUPLICATE_FLAG, "Y")
                        .addField(Tags.GAP_FILL_FLAG, "Y")
                        .addField(Tags.NEW_SEQUENCE_NUMBER, Integer.toString(originalSequenceNumber + 1))
                        .build();
            } else {
                outboundMessage = this.builder
                        .addStandardHeader(MessageType.valueOf(inboundMessage.getMessageType()), request)
                        .addField(Tags.POSSIBLE_DUPLICATE_FLAG, "Y")
                        .build();
            }
            this.sender.send(outboundMessage.toBytes(originalSequenceNumber, DateSupplier.getUtcTimestamp()));
        }
    }
}
