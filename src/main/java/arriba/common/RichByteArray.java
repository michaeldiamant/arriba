package arriba.common;

import java.util.Arrays;

public final class RichByteArray {

    private final byte[] bytes;

    public RichByteArray(final byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        final byte[] copiedBytes = new byte[this.bytes.length];
        System.arraycopy(this.bytes, 0, copiedBytes, 0, copiedBytes.length);

        return copiedBytes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(this.bytes);
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

        return Arrays.equals(this.bytes, ((RichByteArray) obj).bytes);
    }
}
