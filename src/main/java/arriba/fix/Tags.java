package arriba.fix;

public final class Tags {

    private static final int MAXIMUM_TAG = 1000;
    private static final int HEADER_COUNT = 7;
    private static final int TRAILER_COUNT = 1;
    private static final byte[][] DELIMITED_BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];
    private static final byte[][] BYTE_ARRAY_TAGS = new byte[MAXIMUM_TAG][];
    private static final int[] HEADERS = new int[HEADER_COUNT];
    private static final int[] TRAILERS = new int[TRAILER_COUNT];

    // Header tags
    public static final int BEGIN_STRING = 8;
    public static final int BODY_LENGTH = 9;
    public static final int MESSAGE_SEQUENCE_NUMBER = 34;
    public static final int MESSAGE_TYPE = 35;
    public static final int SENDER_COMP_ID = 49;
    public static final int SENDING_TIME = 52;
    public static final int TARGET_COMP_ID = 56;
    // End header tags

    // Body tags
    public static final int ACCOUNT = 1;
    public static final int CLIENT_ORDER_ID = 11;
    public static final int ORDER_QUANTITY = 38;
    public static final int ORDER_TYPE = 40;
    public static final int PRICE = 44;
    public static final int SIDE = 44;
    public static final int SYMBOL = 55;
    public static final int TRANSACTION_TIME = 55;
    public static final int NUMBER_RELATED_SYMBOLS = 146;
    public static final int MD_REQUEST_ID = 262;
    public static final int SUBSCRIPTION_REQUEST_TYPE = 263;
    public static final int MARKET_DEPTH = 264;
    public static final int NUMBER_MD_ENTRIES = 268;
    public static final int MD_ENTRY_TYPE = 269;
    public static final int MD_ENTRY_PRICE = 270;
    public static final int MD_ENTRY_SIZE = 271;
    public static final int USERNAME = 553;
    public static final int PASSWORD = 554;
    // End body tags

    // Trailer tags
    public static final int CHECKSUM = 10;
    // End trailer tags

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
        HEADERS[5] = SENDING_TIME;
        HEADERS[6] = TARGET_COMP_ID;
    }

    private static void buildByteArrayTags() {
        final int equalSignLength = 1;
        for (int tagIndex = 0; tagIndex < MAXIMUM_TAG; tagIndex++) {
            final byte[] tagBytes = Integer.toString(tagIndex).getBytes();

            BYTE_ARRAY_TAGS[tagIndex] = tagBytes;

            final byte[] equalSignDelimitedTagBytes = new byte[tagBytes.length + equalSignLength];

            System.arraycopy(tagBytes, 0, equalSignDelimitedTagBytes, 0, tagBytes.length);
            equalSignDelimitedTagBytes[tagBytes.length] = Fields.EQUAL_SIGN;

            DELIMITED_BYTE_ARRAY_TAGS[tagIndex] = equalSignDelimitedTagBytes;
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
