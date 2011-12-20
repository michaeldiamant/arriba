package arriba.bytearrays;

public final class ByteArrayEntry<V> {

    private final RichByteArray richBytes;
    private final V value;

    public ByteArrayEntry(final RichByteArray richBytes, final V value) {
        this.richBytes = richBytes;
        this.value = value;
    }

    public byte[] getBytes() {
        return this.richBytes.getBytes();
    }

    public V getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.richBytes == null) ? 0 : this.richBytes.hashCode());
        result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        final ByteArrayEntry other = (ByteArrayEntry) obj;
        return this.richBytes.equals(other.richBytes) && this.value.equals(other.value);
    }
}
