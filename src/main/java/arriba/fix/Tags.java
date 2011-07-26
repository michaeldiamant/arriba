package arriba.fix;

public final class Tags {

    private static final int MAXIMUM_TAG = 1000;
    private static final byte[][] BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];

    static {
        final int fieldDelimiterLength = 1;
        for (int tagIndex = 0; tagIndex < MAXIMUM_TAG; tagIndex++) {
            final byte[] tagIndexBytes = Integer.toString(tagIndex).getBytes();

            final byte[] fieldDelimitedTagBytes = new byte[fieldDelimiterLength + tagIndexBytes.length];
            fieldDelimitedTagBytes[0] = Fields.DELIMITER;
            System.arraycopy(tagIndexBytes, 0, fieldDelimitedTagBytes, fieldDelimiterLength, tagIndexBytes.length);

            BYTE_ARRAY_TAGS[tagIndex] = fieldDelimitedTagBytes;
        }
    }

    public static byte[] toByteArray(final int tag) {
        return BYTE_ARRAY_TAGS[tag];
    }
}
