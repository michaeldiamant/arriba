package arriba.fix.chunk.arrays;

import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.tagindexresolvers.TagIndexResolver;

public final class ArrayFixChunkBuilder implements FixChunkBuilder {

    private final byte[][] values;
    private final TagIndexResolver resolver;
    private int maxTagIndex = 0;

    public ArrayFixChunkBuilder(final TagIndexResolver resolver) {
        this.resolver = resolver;

        this.values = new byte[resolver.getTagCount()][];
    }

    @Override
    public FixChunkBuilder addField(final int tag, final byte[] value) {
        final int tagIndex = this.resolver.getTagIndex(tag);
        this.values[tagIndex] = value;

        this.maxTagIndex = Math.max(tagIndex, this.maxTagIndex);
        return this;
    }

    @Override
    public FixChunk build() {
        // TODO Should values be cleared?  Since it is not cleared, if not all required values are provided,
        // then 'garbage' data will be retrieved from the message.
        // The more general question is:  Should validation be enforced at this level?  Currently, no validation exists.

        final int valuesLength = this.maxTagIndex + 1; // Add one to write value at maxTagIndex.
        final byte[][] trimmedValues = new byte[valuesLength][];
        System.arraycopy(this.values, 0, trimmedValues, 0, trimmedValues.length);

        return new ArrayFixChunk(this.resolver, trimmedValues);
    }

    @Override
    public void clear() {
        this.maxTagIndex = 0;
    }
}
