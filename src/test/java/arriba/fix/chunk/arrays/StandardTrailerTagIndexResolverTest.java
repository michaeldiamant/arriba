package arriba.fix.chunk.arrays;

import arriba.fix.Tags;


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
