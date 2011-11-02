package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class RelatedSymbolsTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new RelatedSymbolsTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return new int[] {
                Tags.SYMBOL,
        };
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[0];
    }
}
