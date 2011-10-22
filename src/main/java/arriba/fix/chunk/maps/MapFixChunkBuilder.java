package arriba.fix.chunk.maps;

import java.util.Map;

import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;

import com.google.common.collect.Maps;

public final class MapFixChunkBuilder implements FixChunkBuilder {

    private final Map<Integer, String> tagToValues = Maps.newHashMap();

    @Override
    public FixChunkBuilder addField(final int tag, final String value) {
        this.tagToValues.put(tag, value);

        return this;
    }

    @Override
    public FixChunk build() {
        return new MapFixChunk(this.tagToValues);
    }

    @Override
    public void clear() {
        this.tagToValues.clear();
    }
}
