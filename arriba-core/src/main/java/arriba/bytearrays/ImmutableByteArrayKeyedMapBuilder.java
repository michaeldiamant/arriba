package arriba.bytearrays;


public final class ImmutableByteArrayKeyedMapBuilder<V> {

    private final ByteArrayKeyedMap<V> backingMap = new MutableByteArrayKeyedMap<V>();

    public ImmutableByteArrayKeyedMapBuilder<V> put(final byte[] key, final V value) {
        this.backingMap.put(key, value);

        return this;
    }

    public ImmutableByteArrayKeyedMap<V> build() {
        final ByteArrayKeyedMap<V> backingMapCopy = new MutableByteArrayKeyedMap<V>();

        for (final ByteArrayEntry<V> entry : this.backingMap.entrySet()) {
            backingMapCopy.put(entry.getBytes(), entry.getValue());
        }

        return new ImmutableByteArrayKeyedMap<V>(backingMapCopy);
    }

    public void clear() {
        this.backingMap.clear();
    }
}
