package arriba.disruptor.inbound;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.Fields;
import arriba.fix.RepeatingGroups;
import arriba.fix.Tags;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.RepeatingGroupBuilder;

import com.lmax.disruptor.EventHandler;

public final class DeserializingFixMessageEventHandler implements EventHandler<InboundFixMessageEvent> {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);

    private final InboundFixMessageBuilder inboundFixMessageBuilder;
    private final RepeatingGroupBuilder repeatingGroupBuilder;

    private byte nextFlagByte;
    private int nextFlagIndex;
    private boolean hasFoundFinalDelimiter;
    private int lastDeserializedTag;

    private ParsingState parsingState;
    private int[] repeatingGroupTags;
    private boolean hasFoundNumberOfRepeatingGroupsTag;

    public DeserializingFixMessageEventHandler(final InboundFixMessageBuilder inboundFixMessageBuilder,
            final RepeatingGroupBuilder repeatingGroupBuilder) {
        this.inboundFixMessageBuilder = inboundFixMessageBuilder;
        this.repeatingGroupBuilder = repeatingGroupBuilder;

        this.reset();
    }

    @Override
    public void onEvent(final InboundFixMessageEvent entry, final boolean endOfBatch) throws Exception {
        final ChannelBuffer serializedFixMessage = entry.getSerializedFixMessage();

        this.parsingState = ParsingState.NON_REPEATING_GROUP;
        final InboundFixMessage inboundFixMessage = this.deserializeFixMessage(serializedFixMessage);

        entry.setFixMessage(inboundFixMessage);
    }

    private InboundFixMessage deserializeFixMessage(final ChannelBuffer fixMessageBuffer) throws DeserializationException {
        while ((this.nextFlagIndex = fixMessageBuffer.bytesBefore(this.nextFlagByte)) != -1) {
            final ChannelBuffer nextValueBuffer = fixMessageBuffer.readBytes(this.nextFlagIndex);
            fixMessageBuffer.readerIndex(fixMessageBuffer.readerIndex() + 1);

            if (Fields.EQUAL_SIGN == this.nextFlagByte) {
                this.nextFlagByte = Fields.DELIMITER;

                final byte[] tag = nextValueBuffer.array();
                if (Arrays.equals(CHECKSUM_BYTES, tag)) {
                    this.hasFoundFinalDelimiter = true;
                }
                this.lastDeserializedTag = Integer.parseInt(new String(tag));

                switch (this.parsingState) {
                case REPEATING_GROUP:
                    if (Arrays.binarySearch(this.repeatingGroupTags, this.lastDeserializedTag) < 0) {
                        this.parsingState = ParsingState.NON_REPEATING_GROUP;
                    }

                    break;
                case NON_REPEATING_GROUP:
                    if (null != (this.repeatingGroupTags = RepeatingGroups.NUMBER_IN_GROUP_TAGS[this.lastDeserializedTag])) {
                        this.parsingState = ParsingState.REPEATING_GROUP;
                        this.hasFoundNumberOfRepeatingGroupsTag = true;
                    }

                    break;
                }
            } else if (Fields.DELIMITER == this.nextFlagByte) {
                this.nextFlagByte = Fields.EQUAL_SIGN;

                final byte[] value = nextValueBuffer.array();

                switch (this.parsingState) {
                case REPEATING_GROUP:
                    if (this.hasFoundNumberOfRepeatingGroupsTag) {
                        this.hasFoundNumberOfRepeatingGroupsTag = false;

                        // TODO Optimize this conversion.  Consider caching String-to-int values.
                        final int numberOfRepeatingGroups = Integer.parseInt(new String(value));
                        this.repeatingGroupBuilder.setNumberOfRepeatingGroupsField(this.lastDeserializedTag, numberOfRepeatingGroups);
                    } else {
                        this.repeatingGroupBuilder.addField(this.lastDeserializedTag, value);

                        // TODO Hook in repeating groups into inbound FIX message builder.
                    }

                    break;
                case NON_REPEATING_GROUP:
                    this.inboundFixMessageBuilder.addField(this.lastDeserializedTag, value);
                    if (this.hasFoundFinalDelimiter) {
                        this.hasFoundFinalDelimiter = false;

                        final InboundFixMessage inboundFixMessage =
                                this.inboundFixMessageBuilder.build(this.repeatingGroupBuilder.build(),
                                        this.repeatingGroupBuilder.getNumberOfRepeatingGroupTags());
                        this.reset();

                        return inboundFixMessage;
                    }

                    break;
                }
            }
        }

        // TODO Consider how to better handle this situation.
        throw new DeserializationException("Unable to deserialize FIX message.");
    }

    private void reset() {
        this.nextFlagIndex = -1;
        this.nextFlagByte = Fields.EQUAL_SIGN;
        this.hasFoundFinalDelimiter = false;
        this.lastDeserializedTag = -1;
        this.repeatingGroupTags = null;
        this.inboundFixMessageBuilder.clear();
        this.repeatingGroupBuilder.clear();
    }

    public void onEndOfBatch() throws Exception {
    }

    private static enum ParsingState {
        REPEATING_GROUP, NON_REPEATING_GROUP
    }
}
