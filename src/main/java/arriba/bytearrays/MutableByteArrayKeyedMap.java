package arriba.bytearrays;

import java.util.Map;

import com.google.common.collect.Maps;

public final class MutableByteArrayKeyedMap<V> implements ByteArrayKeyedMap<V> {

    private final Map<RichByteArray, V> bytesToValue = Maps.newHashMap();

    @Override
    public V put(final byte[] key, final V value) {
        return this.bytesToValue.put(new RichByteArray(key), value);
    }

    @Override
    public V get(final byte[] key) {
        return this.bytesToValue.get(new RichByteArray(key));
    }

    @Override
    public void clear() {
        this.bytesToValue.clear();
    }
}
