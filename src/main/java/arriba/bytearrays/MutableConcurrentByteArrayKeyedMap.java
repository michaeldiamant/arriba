package arriba.bytearrays;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public final class MutableConcurrentByteArrayKeyedMap<V> implements ConcurrentByteArrayKeyedMap<V> {

    final ConcurrentMap<RichByteArray, V> bytesToValue = Maps.newConcurrentMap();

    public V putIfAbsent(final byte[] key, final V value) {
        return this.bytesToValue.putIfAbsent(new RichByteArray(key), value);
    }

    @Override
    public V put(final byte[] key, final V value) {
        return this.bytesToValue.put(new RichByteArray(key), value);
    }

    public V get(final byte[] key) {
        return this.bytesToValue.get(key);
    }
}
