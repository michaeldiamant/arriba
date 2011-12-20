package arriba.bytearrays;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

    @Override
    public void clear() {
        this.bytesToValue.clear();
    }

    @Override
    public Set<ByteArrayEntry<V>> entrySet() {
        final Set<ByteArrayEntry<V>> entries = Sets.newHashSet();
        for (final Entry<RichByteArray, V> richEntry : this.bytesToValue.entrySet()) {
            entries.add(new ByteArrayEntry<V>(richEntry.getKey(), richEntry.getValue()));
        }

        return entries;
    }
}
