package arriba.bytearrays;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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

    @Override
    public Set<ByteArrayEntry<V>> entrySet() {
        final Set<ByteArrayEntry<V>> entries = Sets.newHashSet();
        for (final Entry<RichByteArray, V> richEntry : this.bytesToValue.entrySet()) {
            entries.add(new ByteArrayEntry<V>(richEntry.getKey(), richEntry.getValue()));
        }

        return entries;
    }
}
