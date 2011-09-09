package arriba.fix.disruptor;

import arriba.fix.*;
import arriba.fix.chunk.FixChunk;
import arriba.fix.messages.FixMessage;
import com.lmax.disruptor.EventHandler;
import org.jboss.netty.buffer.ChannelBuffer;

import java.util.Arrays;

public class DeserializingFixMessageEntry implements EventHandler<FixMessageEntry> {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);
    private static final byte[] MESSAGE_TYPE_BYTES = Tags.toByteArray(Tags.MESSAGE_TYPE);

    private final FixMessageBuilder<? extends FixChunk> fixMessageBuilder;
    private final RepeatingGroupBuilder groupBuilder = new RepeatingGroupBuilder();

    private byte nextFlagByte;
    private int nextFlagIndex;
    private boolean hasFoundFinalDelimiter;
    private boolean hasFoundMessageType;
    private int lastDeserializedTag;

    private ParsingState parsingState;
    private int[] repeatingGroupTags;
    private boolean hasFoundNumberOfRepeatingGroupsTag;

    public DeserializingFixMessageEntry(final FixMessageBuilder<? extends FixChunk> fixMessageBuilder) {
        this.fixMessageBuilder = fixMessageBuilder;
        this.reset();
    }

  @Override
  public void onEvent(FixMessageEntry entry, boolean b) throws Exception {
        final ChannelBuffer serializedFixMessage = entry.getSerializedFixMessage();

	this.parsingState =  ParsingState.NON_REPEATING_GROUP;
        final FixMessage fixMessage = this.deserializeFixMessage(serializedFixMessage);

        entry.setFixMessage(fixMessage);
    }

    private FixMessage deserializeFixMessage(final ChannelBuffer fixMessageBuffer) throws DeserializationException {
        while ((this.nextFlagIndex = fixMessageBuffer.bytesBefore(this.nextFlagByte)) != -1) {
            final ChannelBuffer nextValueBuffer = fixMessageBuffer.readBytes(this.nextFlagIndex);
            fixMessageBuffer.readerIndex(fixMessageBuffer.readerIndex() + 1);

            if (Fields.EQUAL_SIGN == this.nextFlagByte) {
                this.nextFlagByte = Fields.DELIMITER;

                final byte[] tag = nextValueBuffer.array();
                if (Arrays.equals(CHECKSUM_BYTES, tag)) {
                    this.hasFoundFinalDelimiter = true;
                } else if (Arrays.equals(MESSAGE_TYPE_BYTES, tag)) {
                    this.hasFoundMessageType = true;
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
                        this.groupBuilder.setNumberOfRepeatingGroupsTag(this.lastDeserializedTag);
                    }

                    break;
                }
            } else if (Fields.DELIMITER == this.nextFlagByte) {
                this.nextFlagByte = Fields.EQUAL_SIGN;

                final String value = new String(nextValueBuffer.array());

                switch (this.parsingState) {
                case REPEATING_GROUP:
                    if (this.hasFoundNumberOfRepeatingGroupsTag) {
                        this.hasFoundNumberOfRepeatingGroupsTag = false;
                        this.groupBuilder.setNumberOfRepeatingGroups(Integer.parseInt(value));
                    } else {
                        this.groupBuilder.addField(this.lastDeserializedTag, value);
                    }

                    break;
                case NON_REPEATING_GROUP:
                    this.fixMessageBuilder.addField(this.lastDeserializedTag, value);
                    if (this.hasFoundMessageType) {
                        this.hasFoundMessageType = false;

                        this.fixMessageBuilder.setMessageType(value);
                    } else if (this.hasFoundFinalDelimiter) {
                        this.hasFoundFinalDelimiter = false;

                        final FixMessage fixMessage = this.fixMessageBuilder.build();
                        this.reset();

                        return fixMessage;
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
        this.hasFoundMessageType = false;
        this.lastDeserializedTag = -1;
        this.repeatingGroupTags = null;
        this.fixMessageBuilder.clear();
        this.groupBuilder.clear();
    }

    public void onEndOfBatch() throws Exception {}

  private static enum ParsingState {
        REPEATING_GROUP,
        NON_REPEATING_GROUP
    }
}
