package arriba.bytearrays;

import java.util.Map;

import com.google.common.collect.Maps;

public final class ImmutableByteArrayKeyedMap<V> implements ByteArrayKeyedMap<V> {

    private final Map<RichByteArray, V> bytesToValue;

    public ImmutableByteArrayKeyedMap(final Map<RichByteArray, V> bytesToValue) {
        this.bytesToValue = Maps.newHashMap(bytesToValue);
    }

    @Override
    public V put(final byte[] key, final V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final byte[] key) {
        return this.bytesToValue.get(new RichByteArray(key));
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
