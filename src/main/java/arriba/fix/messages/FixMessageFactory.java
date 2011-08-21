package arriba.fix.messages;

import java.util.Map;

import arriba.fix.chunk.FixChunk;

public final class FixMessageFactory {

    public static FixMessage create(final String messageType, final byte[] beginStringBytes, final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) {

        if ("D".equals(messageType)) {
            return new NewOrderSingle(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
        } else {
            throw new IllegalArgumentException("Message type " + messageType + " does not map to a known FIX message.");
        }
    }
}
