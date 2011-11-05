package arriba.bytearrays;

import java.util.Set;


public interface ByteArrayKeyedMap<V> {

    V put(final byte[] key, final V value);

    V get(final byte[] key);

    void clear();

    Set<ByteArrayEntry<V>> entrySet();
}
