package arriba.fix.outbound;

import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.messages.InboundFixMessage;

public final class RichOutboundFixMessageBuilder {

    private final RawOutboundFixMessageBuilder rawBuilder;
    private String beginString = null;
    private String senderCompId = null;
    private String targetCompId = null;

    public RichOutboundFixMessageBuilder(final RawOutboundFixMessageBuilder rawBuilder) {
        this.rawBuilder = rawBuilder;
    }

    public RichOutboundFixMessageBuilder addStandardHeader(final MessageType messageType, final String beginString,
            final String senderCompId, final String targetCompId) {
        this.beginString = beginString;
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;

        this.rawBuilder
        .addField(Tags.MESSAGE_TYPE, messageType.getValue())
        .addField(Tags.SENDER_COMP_ID, senderCompId)
        .addField(Tags.TARGET_COMP_ID, targetCompId);

        return this;
    }

    public RichOutboundFixMessageBuilder addStandardHeader(final MessageType messageType, final InboundFixMessage message) {
        return this.addStandardHeader(
                messageType,
                message.getHeaderValue(Tags.BEGIN_STRING),
                message.getTargetCompId(),
                message.getSenderCompId()
                );
    }

    public RichOutboundFixMessageBuilder addUtcTimestamp(final int tag) {
        // TODO Improve efficiency final of date writing.
        this.rawBuilder.addField(tag, DateSupplier.getUtcTimestamp());

        return this;
    }

    public RichOutboundFixMessageBuilder addField(final int tag, final String value) {
        this.rawBuilder.addField(tag, value);

        return this;
    }

    public OutboundFixMessage build() {
        final OutboundFixMessage message = this.rawBuilder.build(this.beginString, this.senderCompId, this.targetCompId);

        this.reset();

        return message;
    }

    private void reset() {
        this.senderCompId = null;
        this.targetCompId = null;
    }
}
