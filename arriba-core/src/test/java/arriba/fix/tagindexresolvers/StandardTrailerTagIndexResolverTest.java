package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;
import arriba.fix.tagindexresolvers.StandardTrailerTagIndexResolver;
import arriba.fix.tagindexresolvers.TagIndexResolver;


public class StandardTrailerTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new StandardTrailerTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return Tags.getTrailers();
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[0];
    }
}
