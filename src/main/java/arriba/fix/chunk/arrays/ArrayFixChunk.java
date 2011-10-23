package arriba.fix.chunk.arrays;

import java.io.IOException;
import java.io.OutputStream;

import arriba.fix.chunk.FixChunk;

public final class ArrayFixChunk implements FixChunk {

    private final byte[][] values;
    private final TagIndexResolver resolver;

    protected ArrayFixChunk(final TagIndexResolver resolver, final byte[][] values) {
        this.resolver = resolver;
        this.values = values;
    }

    @Override
    public boolean isDefinedFor(final int tag) {
        return this.resolver.isDefinedFor(tag);
    }

    @Override
    public String getValue(final int tag) {
        return new String(this.values[this.resolver.getTagIndex(tag)]);
    }

    @Override
    public byte[] toByteArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(final OutputStream outputStream) throws IOException {
        throw new UnsupportedOperationException();
    }
}
