package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class MarketDataRequestTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new MarketDataRequestTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return new int[] {
                Tags.MD_REQUEST_ID,
                Tags.SUBSCRIPTION_REQUEST_TYPE,
                Tags.MARKET_DEPTH,
                Tags.NUMBER_MD_ENTRY_TYPES,
                Tags.NUMBER_RELATED_SYMBOLS,
        };
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[] {
                Tags.MD_STREAM_ID
        };
    }
}
