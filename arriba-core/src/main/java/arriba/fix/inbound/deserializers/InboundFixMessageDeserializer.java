package arriba.fix.inbound.deserializers;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.Fields;
import arriba.fix.RepeatingGroups;
import arriba.fix.Tags;
import arriba.fix.inbound.InboundFixMessage;
import arriba.fix.inbound.InboundFixMessageBuilder;
import arriba.fix.inbound.RepeatingGroupBuilder;

public final class InboundFixMessageDeserializer {

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

    public InboundFixMessageDeserializer(final InboundFixMessageBuilder inboundFixMessageBuilder,
            final RepeatingGroupBuilder repeatingGroupBuilder) {
        this.inboundFixMessageBuilder = inboundFixMessageBuilder;
        this.repeatingGroupBuilder = repeatingGroupBuilder;

        this.reset();
    }


    public InboundFixMessage deserialize(final ChannelBuffer buffer) {
        while ((this.nextFlagIndex = buffer.bytesBefore(this.nextFlagByte)) != -1) {
            final ChannelBuffer nextValueBuffer = buffer.readBytes(this.nextFlagIndex);
            buffer.readerIndex(buffer.readerIndex() + 1);

            if (Fields.EQUAL_SIGN == this.nextFlagByte) {
                this.nextFlagByte = Fields.DELIMITER;

                final byte[] tag = nextValueBuffer.array();
                if (Arrays.equals(CHECKSUM_BYTES, tag)) {
                    this.hasFoundFinalDelimiter = true;
                }

                // TODO Make lookup table.
                this.lastDeserializedTag = Integer.parseInt(new String(tag));

                switch (this.parsingState) {
                case REPEATING_GROUP:
                    if (Arrays.binarySearch(this.repeatingGroupTags, this.lastDeserializedTag) < 0) {
                        if (this.hasFoundNumberOfRepeatingGroupsTag()) {
                            this.handleNewRepeatingGroup();
                        } else {
                            this.parsingState = ParsingState.NON_REPEATING_GROUP;
                        }
                    }

                    break;
                case NON_REPEATING_GROUP:
                    if (this.hasFoundNumberOfRepeatingGroupsTag()) {
                        this.handleNewRepeatingGroup();
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

                        this.repeatingGroupBuilder.setNumberOfRepeatingGroupsTag(this.lastDeserializedTag);
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

        return null;
    }

    private void handleNewRepeatingGroup() {
        this.repeatingGroupTags = RepeatingGroups.NUMBER_IN_GROUP_TAGS[this.lastDeserializedTag];
        this.parsingState = ParsingState.REPEATING_GROUP;
        this.hasFoundNumberOfRepeatingGroupsTag = true;
    }

    private boolean hasFoundNumberOfRepeatingGroupsTag() {
        return null != RepeatingGroups.NUMBER_IN_GROUP_TAGS[this.lastDeserializedTag];
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

    private static enum ParsingState {
        REPEATING_GROUP, NON_REPEATING_GROUP
    }
}
