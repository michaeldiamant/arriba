package arriba.fix.chunk.maps;

import java.util.Map;

import arriba.fix.chunk.FixChunkBuilder;

import com.google.common.collect.Maps;

public final class MapFixChunkBuilder implements FixChunkBuilder<MapFixChunk> {

    private final Map<Integer, String> tagToValues = Maps.newHashMap();

    @Override
    public FixChunkBuilder<MapFixChunk> addField(final int tag, final String value) {
        this.tagToValues.put(tag, value);

        return this;
    }

    @Override
    public MapFixChunk build() {
        return new MapFixChunk(this.tagToValues);
    }

    @Override
    public void clear() {
        this.tagToValues.clear();
    }
}
