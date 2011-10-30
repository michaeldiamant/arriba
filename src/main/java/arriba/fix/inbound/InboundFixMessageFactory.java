package arriba.fix.inbound;

import java.util.Arrays;
import java.util.Map;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.MessageType;

public final class InboundFixMessageFactory {

    public static InboundFixMessage create(final FixChunk headerChunk, final FixChunk bodyChunk,
            final FixChunk trailerChunk, final Map<Integer, FixChunk[]> groupCountToGroupChunk) {
        final byte[] messageType = headerChunk.getSerializedValue(Tags.MESSAGE_TYPE);

        // TODO Make constant time.

        if (Arrays.equals(MessageType.NEW_ORDER_SINGLE, messageType)) {
            return new NewOrderSingle(headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if (Arrays.equals(MessageType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, messageType)) {
            return new MarketDataSnapshotFullRefresh(headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if (Arrays.equals(MessageType.LOGON, messageType)) {
            return new Logon(headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if (Arrays.equals(MessageType.MARKET_DATA_REQUEST, messageType)) {
            return new MarketDataRequest(headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else {
            throw new IllegalArgumentException("Message type " + messageType + " does not map to a known FIX message.");
        }
    }
}

