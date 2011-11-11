package arriba.fix.outbound;

import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.InboundFixMessage;

public final class RichOutboundFixMessageBuilder {

    private final RawOutboundFixMessageBuilder rawBuilder;

    public RichOutboundFixMessageBuilder(final RawOutboundFixMessageBuilder rawBuilder) {
        this.rawBuilder = rawBuilder;
    }

    public RichOutboundFixMessageBuilder addStandardHeader(final MessageType messageType, final String beginString,
            final String senderCompId, final String targetCompId) {
        this.rawBuilder
        .addField(Tags.BEGIN_STRING, beginString)
        .addField(Tags.MESSAGE_TYPE, messageType.getValue())
        .setSenderCompId(senderCompId)
        .setTargetCompId(targetCompId);

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
        return this.rawBuilder.build();
    }
}
