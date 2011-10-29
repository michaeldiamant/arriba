package arriba.fix.fields;

public final class MessageType {

    public static byte[] LOGON = "A".getBytes();
    public static byte[] NEW_ORDER_SINGLE = "D".getBytes();
    public static final byte[] MARKET_DATA_SNAPSHOT_FULL_REFRESH = "W".getBytes();
    public static final byte[] MARKET_DATA_REQUEST = "V".getBytes();
}
