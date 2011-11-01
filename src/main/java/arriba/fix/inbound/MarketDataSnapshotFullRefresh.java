package arriba.fix.inbound;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class MarketDataSnapshotFullRefresh extends InboundFixMessage {

    public MarketDataSnapshotFullRefresh(final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getSymbol() {
        return this.getBodyValue(Tags.SYMBOL);
    }

}
