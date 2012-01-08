package arriba.fix.chunk.arrays;

import arriba.fix.chunk.FixChunk;
import arriba.fix.tagindexresolvers.TagIndexResolver;

public final class ArrayFixChunk implements FixChunk {

    private static final byte[] EMPTY_CHUNK = new byte[0];

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
    public byte[] getSerializedValue(final int tag) {
        final int tagIndex = this.resolver.getTagIndex(tag);
        return tagIndex < this.values.length ? this.values[this.resolver.getTagIndex(tag)] : EMPTY_CHUNK;
    }

    @Override
    public String getValue(final int tag) {
        return new String(this.getSerializedValue(tag));
    }
}
