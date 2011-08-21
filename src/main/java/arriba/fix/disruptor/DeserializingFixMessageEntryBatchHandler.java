package arriba.fix.disruptor;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;

import arriba.fix.Fields;
import arriba.fix.FixMessageBuilder;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.messages.FixMessage;

import com.lmax.disruptor.BatchHandler;

public class DeserializingFixMessageEntryBatchHandler implements BatchHandler<FixMessageEntry> {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);
    private static final byte[] MESSAGE_TYPE_BYTES = Tags.toByteArray(Tags.MESSAGE_TYPE);

    private final FixMessageBuilder<? extends FixChunk> fixMessageBuilder;

    private byte nextFlagByte;
    private int nextFlagIndex;
    private boolean hasFoundFinalDelimiter;
    private boolean hasFoundMessageType;
    private int lastDeserializedTag;

    public DeserializingFixMessageEntryBatchHandler(final FixMessageBuilder<? extends FixChunk> fixMessageBuilder) {
        this.fixMessageBuilder = fixMessageBuilder;
        this.reset();
    }

    public void onAvailable(final FixMessageEntry entry) throws Exception {
        final ChannelBuffer serializedFixMessage = entry.getSerializedFixMessage();

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
            } else if (Fields.DELIMITER == this.nextFlagByte) {
                this.nextFlagByte = Fields.EQUAL_SIGN;

                final String value = new String(nextValueBuffer.array());

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
        this.fixMessageBuilder.clear();
    }

    public void onEndOfBatch() throws Exception {}
}
