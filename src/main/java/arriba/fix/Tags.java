package arriba.fix;

public final class Tags {

    private static final int MAXIMUM_TAG = 1000;
    private static final int HEADER_COUNT = 6;
    private static final int TRAILER_COUNT = 1;
    private static final byte[][] DELIMITED_BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];
    private static final byte[][] BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];
    private static final int[] HEADERS = new int[HEADER_COUNT];
    private static final int[] TRAILERS = new int[TRAILER_COUNT];

    public static final int BEGIN_STRING = 8;
    public static final int BODY_LENGTH = 9;
    public static final int CHECKSUM = 10;
    public static final int MESSAGE_SEQUENCE_NUMBER = 34;
    public static final int MESSAGE_TYPE = 35;
    public static final int SENDER_COMP_ID = 49;
    public static final int TARGET_COMP_ID = 56;

    static {
        buildByteArrayTags();
        buildHeaders();
        buildTrailers();
    }

    private static void buildTrailers() {
        TRAILERS[0] = CHECKSUM;
    }

    private static void buildHeaders() {
        HEADERS[0] = BEGIN_STRING;
        HEADERS[1] = BODY_LENGTH;
        HEADERS[2] = MESSAGE_SEQUENCE_NUMBER;
        HEADERS[3] = MESSAGE_TYPE;
        HEADERS[4] = SENDER_COMP_ID;
        HEADERS[5] = TARGET_COMP_ID;
    }

    private static void buildByteArrayTags() {
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

    public static int[] getHeaders() {
        final int[] headers = new int[HEADER_COUNT];
        System.arraycopy(HEADERS, 0, headers, 0, headers.length);

        return headers;
    }

    public static int[] getTrailers() {
        final int[] trailers = new int[TRAILER_COUNT];
        System.arraycopy(TRAILERS, 0, trailers, 0, trailers.length);

        return trailers;
    }
}
