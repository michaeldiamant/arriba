package arriba.fix.inbound;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class MarketDataRequest extends InboundFixMessage {

    public MarketDataRequest(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getMdReqId() {
        return this.getBodyValue(Tags.MD_REQUEST_ID);
    }

    public String getSubscriptionRequestType() {
        return this.getBodyValue(Tags.SUBSCRIPTION_REQUEST_TYPE);
    }

    public String getMarketDepth() {
        return this.getBodyValue(Tags.MARKET_DEPTH);
    }

    public FixChunk[] getMdEntries() {
        return this.getGroup(Tags.NUMBER_MD_ENTRIES);
    }

    public FixChunk[] getRelatedSymbols() {
        return this.getGroup(Tags.NUMBER_RELATED_SYMBOLS);
    }
}
