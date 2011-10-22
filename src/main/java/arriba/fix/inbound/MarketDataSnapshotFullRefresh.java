package arriba.fix.inbound;

import java.util.Map;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class MarketDataSnapshotFullRefresh extends InboundFixMessage {

    public MarketDataSnapshotFullRefresh(final byte[] beginStringBytes, final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) {
        super(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
    }

    public String getSymbol() {
        return this.getBodyValue(Tags.SYMBOL);
    }

}
