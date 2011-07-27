package arriba.fix;

public final class Tags {

    private static final int MAXIMUM_TAG = 1000;
    private static final byte[][] DELIMITED_BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];
    private static final byte[][] BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];

    public static final int CHECKSUM = 10;
    public static final int MESSAGE_TYPE = 35;

    static {
        final int fieldDelimiterLength = 1;
        for (int tagIndex = 0; tagIndex < MAXIMUM_TAG; tagIndex++) {
            final byte[] tagBytes = Integer.toString(tagIndex).getBytes();

            BYTE_ARRAY_TAGS[tagIndex] = tagBytes;

            final byte[] fieldDelimitedTagBytes = new byte[fieldDelimiterLength + tagBytes.length];
            fieldDelimitedTagBytes[0] = Fields.DELIMITER;
            System.arraycopy(tagBytes, 0, fieldDelimitedTagBytes, fieldDelimiterLength, tagBytes.length);

            DELIMITED_BYTE_ARRAY_TAGS[tagIndex] = fieldDelimitedTagBytes;
        }
    }

    public static byte[] toDelimitedByteArray(final int tag) {
        return DELIMITED_BYTE_ARRAY_TAGS[tag];
    }

    public static byte[] toByteArray(final int tag) {
        return BYTE_ARRAY_TAGS[tag];
    }
}
