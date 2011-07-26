package arriba.fix;

public final class SerializedField {

    private final byte[] tag;
    private final byte[] value;

    public SerializedField(final byte[] tag, final byte[] value) {
        this.tag = tag;
        this.value = value;
    }

    public byte[] getTag() {
        return this.tag;
    }

    public byte[] getValue() {
        return this.value;
    }
}
