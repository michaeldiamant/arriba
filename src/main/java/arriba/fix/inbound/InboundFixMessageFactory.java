package arriba.fix.inbound;

import java.util.Map;

import arriba.fix.chunk.FixChunk;

public final class InboundFixMessageFactory {

    public static InboundFixMessage create(final String messageType, final byte[] beginStringBytes, final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) {

        // TODO Replace literals with constants and try to move to switch statements (e.g. Java 7).
        if ("D".equals(messageType)) {
            return new NewOrderSingle(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if ("W".equals(messageType)) {
            return new MarketDataSnapshotFullRefresh(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if ("A".equals(messageType)) {
            return new Logon(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else if ("V".equals(messageType)) {
            return new MarketDataRequest(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else {
            throw new IllegalArgumentException("Message type " + messageType + " does not map to a known FIX message.");
        }
    }
}
