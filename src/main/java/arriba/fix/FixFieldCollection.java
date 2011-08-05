package arriba.fix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// TODO Implement Map<K, V>.
public class FixFieldCollection {

    private final int[] headerTagArray;
    private final String[] headerValueArray;
    private final int[] bodyTagArray;
    private final String[] bodyValueArray;
    private final int[] trailerTagArray;
    private final String[] trailerValueArray;

    private FixFieldCollection(final List<Field<String>> headerFields, final List<Field<String>> bodyFields,
            final List<Field<String>> trailerFields) {
        this.headerTagArray = new int[headerFields.size()];
        this.headerValueArray = new String[headerFields.size()];
        populate(this.headerTagArray, this.headerValueArray, headerFields);

        this.bodyTagArray = new int[bodyFields.size()];
        this.bodyValueArray = new String[bodyFields.size()];
        populate(this.bodyTagArray, this.bodyValueArray, bodyFields);

        this.trailerTagArray = new int[trailerFields.size()];
        this.trailerValueArray = new String[trailerFields.size()];
        populate(this.trailerTagArray, this.trailerValueArray, trailerFields);
    }

    private static void populate(final int[] tagArray, final String[] valueArray, final List<Field<String>> fields) {
        int arrayIndex = 0;
        for (final Field<String> field : fields) {
            tagArray[arrayIndex] = field.getTag();
            valueArray[arrayIndex] = field.getValue();
            ++arrayIndex;
        }
    }

    public byte[] toByteArray() {
        // TODO Performance test ByteArrayOutputStream vs building LinkedList of all bytes to be written
        // and performing one allocation / write.
        try {
            final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
            write(this.headerTagArray, this.headerValueArray, messageBytes);
            write(this.bodyTagArray, this.bodyValueArray, messageBytes);
            write(this.trailerTagArray, this.trailerValueArray, messageBytes);

            return messageBytes.toByteArray();
        } catch (final IOException e) {
            // FIXME What should happen here?  This is an unrecoverable error.
            return new byte[0];
        }
    }

    private static void write(final int[] tagArray, final String[] valueArray, final OutputStream outputStream) throws IOException {
        for (int tagIndex = 0; tagIndex < tagArray.length; tagIndex++) {
            final int tag = tagArray[tagIndex];
            final String value = valueArray[tagIndex];

            final byte[] tagBytes = Tags.toByteArray(tag);
            final byte[] valueBytes = value.getBytes();

            outputStream.write(tagBytes);
            outputStream.write(Fields.EQUAL_SIGN);
            outputStream.write(valueBytes);
            outputStream.write(Fields.DELIMITER);
        }
    }

    private static String search(final int[] tagArray, final String[] valueArray, final int tag) {
        final int valueIndex = Arrays.binarySearch(tagArray, tag);

        // TODO Handle not finding a tag.
        return valueIndex < 0 ? "" : valueArray[valueIndex];
    }

    public String getTrailerValue(final int tag) {
        return search(this.trailerTagArray, this.trailerValueArray, tag);
    }

    public String getBodyValue(final int tag) {
        return search(this.bodyTagArray, this.bodyValueArray, tag);
    }

    public String getHeaderValue(final int tag) {
        return search(this.headerTagArray, this.headerValueArray, tag);
    }

    public String getValue(final int tag) {
        final String headerValue = this.getHeaderValue(tag);
        if (!headerValue.isEmpty()) {
            return headerValue;
        }

        final String bodyValue = this.getBodyValue(tag);
        if (!bodyValue.isEmpty()) {
            return bodyValue;
        }

        return this.getTrailerValue(tag);
    }

    public static class Builder {

        private static final int[] HEADER_TAGS = Tags.getHeaders();
        private static final int[] TRAILER_TAGS = Tags.getTrailers();

        private final List<Field<String>> headerFields = new LinkedList<Field<String>>();
        private final List<Field<String>> bodyFields = new LinkedList<Field<String>>();
        private final List<Field<String>> trailerFields = new LinkedList<Field<String>>();

        public Builder() {}

        public Builder addField(final int tag, final String value) {
            final Field<String> field = new Field<String>(tag, value);

            if (Arrays.binarySearch(HEADER_TAGS, tag) >= 0) {
                this.headerFields.add(field);
            } else if (Arrays.binarySearch(TRAILER_TAGS, tag) >= 0) {
                this.trailerFields.add(field);
            } else {
                this.bodyFields.add(field);
            }

            return this;
        }

        public FixFieldCollection build() {
            return new FixFieldCollection(this.headerFields, this.bodyFields, this.trailerFields);
        }
    }
}
