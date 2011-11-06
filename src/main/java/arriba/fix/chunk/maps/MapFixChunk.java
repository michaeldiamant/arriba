package arriba.fix.chunk.maps;

import java.util.Map;

import arriba.fix.chunk.FixChunk;

import com.google.common.collect.Maps;

public final class MapFixChunk implements FixChunk {

    private final Map<Integer, byte[]> tagToValues;

    public MapFixChunk(final Map<Integer, byte[]> tagToValues) {
        this.tagToValues = Maps.newHashMap(tagToValues);
    }

    @Override
    public boolean isDefinedFor(final int tag) {
        return this.tagToValues.containsKey(tag);
    }

    @Override
    public String getValue(final int tag) {
        return new String(this.tagToValues.get(tag));
    }

    @Override
    public byte[] getSerializedValue(final int tag) {
        return this.tagToValues.get(tag);
    }
}
