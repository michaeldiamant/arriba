package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class RelatedSymbolsTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.SYMBOL,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
