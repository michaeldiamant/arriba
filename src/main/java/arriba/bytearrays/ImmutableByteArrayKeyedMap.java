package arriba.bytearrays;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ImmutableByteArrayKeyedMap<V> implements ByteArrayKeyedMap<V> {

    private final Map<RichByteArray, V> bytesToValue;

    public ImmutableByteArrayKeyedMap(final ByteArrayKeyedMap<V> bytesToValue) {
        this.bytesToValue = Maps.newHashMap();

        for (final ByteArrayEntry<V> entry : bytesToValue.entrySet()) {
            this.bytesToValue.put(new RichByteArray(entry.getBytes()), entry.getValue());
        }
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

    @Override
    public Set<ByteArrayEntry<V>> entrySet() {
        final Set<ByteArrayEntry<V>> entries = Sets.newHashSet();
        for (final Entry<RichByteArray, V> richEntry : this.bytesToValue.entrySet()) {
            entries.add(new ByteArrayEntry<V>(richEntry.getKey(), richEntry.getValue()));
        }

        return entries;
    }
}
