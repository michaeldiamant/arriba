package arriba.bytearrays;

public interface ConcurrentByteArrayKeyedMap<V> extends ByteArrayKeyedMap<V> {

    V putIfAbsent(final byte[] key, final V value);

}
