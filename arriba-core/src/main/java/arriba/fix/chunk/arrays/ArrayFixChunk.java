package arriba.fix.chunk.arrays;

import arriba.fix.chunk.FixChunk;
import arriba.fix.tagindexresolvers.TagIndexResolver;

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
    public byte[] getSerializedValue(final int tag) {
        return this.values[this.resolver.getTagIndex(tag)];
    }

    @Override
    public String getValue(final int tag) {
        return new String(this.values[this.resolver.getTagIndex(tag)]);
    }
}
