package arriba.fix.messages;

import java.util.Map;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class MarketDataSnapshotFullRefresh extends FixMessage {

    public MarketDataSnapshotFullRefresh(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) {
        super(headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
    }

    public String getSymbol() {
        return this.getBodyValue(Tags.SYMBOL);
    }

}