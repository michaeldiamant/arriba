package arriba.fix.outbound;

import arriba.fix.Tags;
import arriba.fix.fields.MessageType;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.sequencenumbers.SequenceNumberGenerator;

public final class RichOutboundFixMessageBuilder {

    private final RawOutboundFixMessageBuilder rawBuilder;
    private final SequenceNumberGenerator generator;

    public RichOutboundFixMessageBuilder(final RawOutboundFixMessageBuilder rawBuilder, final SequenceNumberGenerator generator) {
        this.rawBuilder = rawBuilder;
        this.generator = generator;
    }

    public RichOutboundFixMessageBuilder addStandardHeader(final MessageType messageType, final InboundFixMessage message) {
        this.rawBuilder
        .addField(Tags.MESSAGE_TYPE, messageType.getValue())
        .setSenderCompId(message.getTargetCompId())
        .setTargetCompId(message.getSenderCompId());

        return this;
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
        this.addUtcTimestamp(Tags.SENDING_TIME);

        this.rawBuilder
        .addField(Tags.MESSAGE_SEQUENCE_NUMBER, Long.toString(this.generator.get()));


        return this.rawBuilder.build();
    }
}
