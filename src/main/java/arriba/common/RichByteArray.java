package arriba.common;

import java.util.Arrays;

public final class RichByteArray {

    private final byte[] bytes;
    private final int hashCode;

    public RichByteArray(final byte[] bytes) {
        this.bytes = bytes;

        final int prime = 31;
        final int result = 1;
        this.hashCode = prime * result + Arrays.hashCode(this.bytes);
    }

    public byte[] getBytes() {
        final byte[] copiedBytes = new byte[this.bytes.length];
        System.arraycopy(this.bytes, 0, copiedBytes, 0, copiedBytes.length);

        return copiedBytes;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
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

        return Arrays.equals(this.bytes, ((RichByteArray) obj).bytes);
    }
}
