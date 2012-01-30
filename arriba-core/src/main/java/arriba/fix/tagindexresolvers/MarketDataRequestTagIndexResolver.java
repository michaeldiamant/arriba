package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class MarketDataRequestTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.MD_REQUEST_ID,
                Tags.SUBSCRIPTION_REQUEST_TYPE,
                Tags.MARKET_DEPTH,
                Tags.NUMBER_MD_ENTRY_TYPES,
                Tags.NUMBER_RELATED_SYMBOLS,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[] {
                Tags.MD_STREAM_ID
        };
    }
}
