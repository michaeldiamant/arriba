package arriba.fix.outbound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import arriba.fix.Fields;
import arriba.fix.Tags;

/**
 * An outbound FIX message builder constructs messages to be sent in byte[] format.
 */
public final class RawOutboundFixMessageBuilder {

    private static final int MAX_MESSAGE_FIELDS_COUNT = 1000;
    private static final int FIELD_DELIMITER_LENGTH = 1;

    private final byte[][] tags = new byte[MAX_MESSAGE_FIELDS_COUNT][];
    private final String[] values = new String[MAX_MESSAGE_FIELDS_COUNT];

    private String targetCompId = null;

    private int readIndex = 0;
    private int messageLength = 0;

    public RawOutboundFixMessageBuilder addField(final int tag, final String value) {
        final byte[] tagBytes = Tags.toDelimitedByteArray(tag);
        this.tags[this.readIndex] = tagBytes;
        this.values[this.readIndex] = value;

        // TODO Alternatively, the bytes of value can retrieved now and the field delimiter
        // byte can be appended now rather than during build() invocation.

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
            final ByteArrayOutputStream out = new ByteArrayOutputStream(this.messageLength);
            int checksum = 0;

            for (int writeIndex = 0; writeIndex < this.readIndex; writeIndex++) {
                out.write(this.tags[writeIndex]);
                checksum += calculateSum(this.tags[writeIndex]);

                final String value = this.values[writeIndex];
                final int valueLength = value.length();
                for (int valueIndex = 0; valueIndex < valueLength; valueIndex++) {
                    final byte byteValue = (byte) value.charAt(valueIndex);

                    out.write(byteValue);
                    checksum += byteValue;
                }

                out.write(Fields.DELIMITER);
                checksum += Fields.DELIMITER;
            }

            checksum = checksum % 256;
            out.write(String.valueOf(checksum).getBytes()); // TODO Create lookup table.
            out.write(Fields.DELIMITER);

            // TODO Create ByteArrayOutputStream implementation that skips deep copy on toByteArray().
            // See http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
            final OutboundFixMessage message = new OutboundFixMessage(out.toByteArray(), this.targetCompId);

            this.reset();

            return message;
        } catch (final IOException e) {
            return new OutboundFixMessage(new byte[0], this.targetCompId);
        }
    }

    private static int calculateSum(final byte[] bytes) {
        int sum = 0;
        final int bytesLength = bytes.length;
        for (int bytesIndex = 0; bytesIndex < bytesLength; bytesIndex++) {
            sum += bytes[bytesIndex];
        }

        return sum;
    }

    private void reset() {
        this.readIndex = 0;
        this.messageLength = 0;
        this.targetCompId = null;
    }
}
