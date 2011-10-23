package arriba.fix.chunk.maps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import arriba.fix.Fields;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

import com.google.common.collect.Maps;

public final class MapFixChunk implements FixChunk {

    private final Map<Integer, byte[]> tagToValues;

    public MapFixChunk(final Map<Integer, byte[]> tagToValues) {
        this.tagToValues = Maps.newHashMap(tagToValues);
    }

    @Override
    public String getValue(final int tag) {
        return new String(this.tagToValues.get(this.tagToValues));
    }

    @Override
    public byte[] toByteArray() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            write(this.tagToValues, outputStream);

            return outputStream.toByteArray();
        } catch (final IOException e) {
            return new byte[0];
        }
    }

    @Override
    public void write(final OutputStream outputStream) throws IOException {
        write(this.tagToValues, outputStream);
    }

    private static void write(final Map<Integer, byte[]> tagToValues, final OutputStream outputStream) throws IOException {
        for (final Entry<Integer, byte[]> tagToValue : tagToValues.entrySet()) {
            final byte[] tagBytes = Tags.toByteArray(tagToValue.getKey());
            final byte[] valueBytes = tagToValue.getValue();

            outputStream.write(tagBytes);
            outputStream.write(Fields.EQUAL_SIGN);
            outputStream.write(valueBytes);
            outputStream.write(Fields.DELIMITER);
        }
    }
}
