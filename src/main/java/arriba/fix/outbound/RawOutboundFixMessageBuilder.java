package arriba.fix.outbound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import arriba.fix.Tags;

/**
 * A raw outbound FIX message builder constructs messages to be sent in byte[] format.
 */
public final class RawOutboundFixMessageBuilder {

    private static final int[] HEADER_TAGS = Tags.getHeaders();
    private static final int MAX_MESSAGE_FIELDS_COUNT = 1000;
    private static final int FIELD_DELIMITER_LENGTH = 1;

    private final byte[][] tags = new byte[MAX_MESSAGE_FIELDS_COUNT][];
    private final String[] values = new String[MAX_MESSAGE_FIELDS_COUNT];

    private String targetCompId = null;

    private int readIndex = 0;
    private int messageLength = 0;
    private int lastHeaderFieldIndex = 0;

    public RawOutboundFixMessageBuilder addField(final int tag, final String value) {
        final byte[] tagBytes = Tags.toDelimitedByteArray(tag);
        this.tags[this.readIndex] = tagBytes;
        this.values[this.readIndex] = value;

        // TODO Alternatively, the bytes of value can retrieved now and the field delimiter
        // byte can be appended now rather than during build() invocation.

        if (Arrays.binarySearch(HEADER_TAGS, tag) >= 0) {
            this.lastHeaderFieldIndex = this.readIndex;
        }

        ++this.readIndex;
        this.messageLength = tagBytes.length + value.length() + FIELD_DELIMITER_LENGTH;

        return this;
    }

    public RawOutboundFixMessageBuilder setTargetCompId(final String targetCompId) {
        this.targetCompId = targetCompId;

        return this.addField(Tags.TARGET_COMP_ID, targetCompId);
    }

    public OutboundFixMessage build() {
        if (null == this.targetCompId) {
            throw new IllegalStateException("Target component ID must be specified.");
        }

        try {
            // TODO Extend to allow underlying array to be resized.  This will enable
            // reuse of one ByteArrayOutputStream instance.
            final ByteArrayOutputStream headerOut = new ByteArrayOutputStream(this.messageLength);
            int messageBytesSum = 0;

            for (int writeIndex = 0; writeIndex <= this.lastHeaderFieldIndex; writeIndex++) {
                messageBytesSum += FieldWriter.write(this.tags[writeIndex], this.values[writeIndex], headerOut);
            }

            // TODO Create ByteArrayOutputStream implementation that skips deep copy on toByteArray().
            // See http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
            final ByteArrayOutputStream nonHeaderOut = new ByteArrayOutputStream(this.messageLength);

            final int remainingIndexes = this.readIndex - this.lastHeaderFieldIndex;
            for (int writeIndex = 0; writeIndex < remainingIndexes; writeIndex++) {
                messageBytesSum += FieldWriter.write(this.tags[writeIndex], this.values[writeIndex], nonHeaderOut);
            }

            final OutboundFixMessage message = new OutboundFixMessage(headerOut, nonHeaderOut, messageBytesSum, this.targetCompId);

            this.reset();

            return message;
        } catch (final IOException e) {
            return new OutboundFixMessage(new ByteArrayOutputStream(), new ByteArrayOutputStream(), 0, this.targetCompId);
        }
    }


    private void reset() {
        this.lastHeaderFieldIndex = 0;
        this.readIndex = 0;
        this.messageLength = 0;
        this.targetCompId = null;
    }
}
