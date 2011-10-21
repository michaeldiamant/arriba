package arriba.fix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * An outgoing FIX message builder constructs messages to be sent in byte[] format.
 */
public final class OutgoingFixMessageBuilder {

    private static final int MAX_MESSAGE_FIELDS_COUNT = 1000;
    private static final int FIELD_DELIMITER_LENGTH = 1;

    private final byte[][] tags = new byte[MAX_MESSAGE_FIELDS_COUNT][];
    private final String[] values = new String[MAX_MESSAGE_FIELDS_COUNT];
    private int readIndex = 0;
    private int messageLength = 0;

    public OutgoingFixMessageBuilder addField(final int tag, final String value) {
        final byte[] tagBytes = Tags.toDelimitedByteArray(tag);
        this.tags[this.readIndex] = tagBytes;
        this.values[this.readIndex] = value;

        // TODO Alternatively, the bytes of value can retrieved now and the field delimiter
        // byte can be appended now rather than during build() invocation.

        ++this.readIndex;
        this.messageLength = tagBytes.length + value.length() + FIELD_DELIMITER_LENGTH;

        return this;
    }

    public byte[] build() {
        try {
            // TODO Extend to allow underlying array to be resized.  This will enable
            // reuse of one ByteArrayOutputStream instance.
            final ByteArrayOutputStream out = new ByteArrayOutputStream(this.messageLength);

            for (int writeIndex = 0; writeIndex < this.readIndex; writeIndex++) {
                out.write(this.tags[writeIndex]);

                final String value = this.values[writeIndex];
                final int valueLength = value.length();
                for (int valueIndex = 0 ; valueIndex < valueLength ; valueIndex++) {
                    out.write((byte)value.charAt(valueIndex));
                }

                out.write(Fields.DELIMITER);
            }

            this.readIndex = 0;
            this.messageLength = 0;

            // TODO Create ByteArrayOutputStream implementation that skips deep copy on toByteArray().
            // See http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
            System.out.println(">> " + new String(out.toByteArray()));
            return out.toByteArray();
        } catch (final IOException e) {
            return new byte[0];
        }
    }
}
