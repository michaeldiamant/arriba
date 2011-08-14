package arriba.fix.chunk.arrays;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import arriba.fix.Fields;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class ArrayFixChunk implements FixChunk {

    private final int[] tagArray;
    private final String[] valueArray;

    protected ArrayFixChunk(final int[] tagArray, final String[] valueArray) {
        this.tagArray = tagArray;
        this.valueArray = valueArray;
    }

    @Override
    public String getValue(final int tag) {
        return search(this.tagArray, this.valueArray, tag);
    }

    private static String search(final int[] tagArray, final String[] valueArray, final int tag) {
        int valueIndex = -1;
        for (int tagIndex = tagArray.length - 1; tagIndex >= 0; tagIndex--) {
            if (tag == tagArray[tagIndex]) {
                valueIndex = tagIndex;
                break;
            }
        }

        // TODO Handle not finding a tag.
        return valueIndex < 0 ? "" : valueArray[valueIndex];
    }

    @Override
    public byte[] toByteArray() {
        // TODO Performance test ByteArrayOutputStream vs building LinkedList of all bytes to be written
        // and performing one allocation / write.
        try {
            final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
            write(this.tagArray, this.valueArray, messageBytes);
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

    @Override
    public void write(final OutputStream outputStream) throws IOException {
        write(this.tagArray, this.valueArray, outputStream);
    }

}
