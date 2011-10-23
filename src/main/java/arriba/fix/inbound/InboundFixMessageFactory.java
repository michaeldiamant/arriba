package arriba.fix.inbound;

import java.util.Arrays;
import java.util.Map;

import arriba.fix.chunk.FixChunk;

public final class InboundFixMessageFactory {

    private static final byte[] LOGON = "A".getBytes();
    private static final byte[] MARKET_DATA_SNAPSHOT_FULL_REFRESH = "W".getBytes();
    private static final byte[] MARKET_DATA_REQUEST = "V".getBytes();
    private static final byte[] NEW_ORDER_SINGLE = "D".getBytes();

    public static InboundFixMessage create(final byte[] messageType, final byte[] beginStringBytes, final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) {

        // TODO Replace literals with constants and try to move to switch statements (e.g. Java 7).
        if (Arrays.equals(NEW_ORDER_SINGLE, messageType)) {
            return new NewOrderSingle(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if (Arrays.equals(MARKET_DATA_SNAPSHOT_FULL_REFRESH, messageType)) {
            return new MarketDataSnapshotFullRefresh(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if (Arrays.equals(LOGON, messageType)) {
            return new Logon(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if (Arrays.equals(MARKET_DATA_REQUEST, messageType)) {
            return new MarketDataRequest(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else {
            throw new IllegalArgumentException("Message type " + messageType + " does not map to a known FIX message.");
        }
    }
}
