package arriba.fix.chunk.arrays;

import arriba.fix.chunk.FixChunkBuilder;

public final class ArrayFixChunkBuilder implements FixChunkBuilder<ArrayFixChunk> {

    @Override
    public FixChunkBuilder<ArrayFixChunk> addField(final int tag, final String value) {

        // TODO Store field.

        return this;
    }

    @Override
    public ArrayFixChunk build() {
        return new ArrayFixChunk(null, null);
    }

    @Override
    public void clear() {

    }
}
