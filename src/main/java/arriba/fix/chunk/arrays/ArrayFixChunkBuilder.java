package arriba.fix.chunk.arrays;

import java.util.Map;
import java.util.Map.Entry;

import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;

import com.google.common.collect.Maps;

public final class ArrayFixChunkBuilder implements FixChunkBuilder {

    private final Map<Integer, String> tagToField = Maps.newHashMap();

    @Override
    public FixChunkBuilder addField(final int tag, final String value) {
        this.tagToField.put(tag, value);

        return this;
    }

    @Override
    public FixChunk build() {
        final int[] tags = new int[this.tagToField.size()];
        final String[] values = new String[this.tagToField.size()];
        int index = 0;
        for (final Entry<Integer, String> entry : this.tagToField.entrySet()) {
            tags[index] = entry.getKey();
            values[index] = entry.getValue();
            ++index;
        }

        return new ArrayFixChunk(tags, values);
    }

    @Override
    public void clear() {
        this.tagToField.clear();
    }
}
