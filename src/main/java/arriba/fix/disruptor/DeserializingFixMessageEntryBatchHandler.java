package arriba.fix.disruptor;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import arriba.fix.Fields;
import arriba.fix.FixFieldCollection;
import arriba.fix.Tags;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.FixMessageFactory;

import com.lmax.disruptor.BatchHandler;

public class DeserializingFixMessageEntryBatchHandler implements BatchHandler<FixMessageEntry> {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);
    private static final byte[] MESSAGE_TYPE_BYTES = Tags.toByteArray(Tags.MESSAGE_TYPE);

    private byte nextFlagByte;
    private int nextFlagIndex;
    private boolean hasFoundFinalDelimiter;
    private boolean hasFoundMessageType;
    private String messageType;
    private FixFieldCollection.Builder fixFieldCollectionBuilder;
    private int lastDeserializedTag;

    public DeserializingFixMessageEntryBatchHandler() {
        this.reset();
    }

    public void onAvailable(final FixMessageEntry entry) throws Exception {
        final byte[] serializedFixMessage = entry.getSerializedFixMessage();
        final ChannelBuffer fixMessageBuffer = ChannelBuffers.copiedBuffer(serializedFixMessage);

        final FixMessage fixMessage = this.deserializeFixMessage(fixMessageBuffer);

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

                this.fixFieldCollectionBuilder.addField(this.lastDeserializedTag, value);
                if (this.hasFoundMessageType) {
                    this.hasFoundMessageType = false;

                    this.messageType = value;
                }
                if (this.hasFoundFinalDelimiter) {
                    this.hasFoundFinalDelimiter = false;

                    this.reset();

                    return FixMessageFactory.create(this.fixFieldCollectionBuilder.build(), this.messageType);
                }
            }
        }

        // TODO Consider how to better handle this situation.
        throw new DeserializationException("Unable to deserialize FIX message.");
    }

    private void reset() {
        this.nextFlagIndex = -1;
        this.nextFlagByte = Fields.DELIMITER;
        this.hasFoundFinalDelimiter = false;
        this.hasFoundMessageType = false;
        this.messageType = "";
        this.fixFieldCollectionBuilder = new FixFieldCollection.Builder();
        this.lastDeserializedTag = -1;
    }

    public void onEndOfBatch() throws Exception {}
}
