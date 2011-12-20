package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class MarketDataSnapshotFullRefreshTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.SYMBOL,
                Tags.NUMBER_MD_ENTRIES,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[] {
                Tags.MD_REQUEST_ID,
                Tags.MD_STREAM_ID
        };
    }
}
