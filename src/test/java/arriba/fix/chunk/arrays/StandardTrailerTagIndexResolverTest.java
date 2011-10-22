package arriba.fix.chunk.arrays;

import arriba.fix.Tags;


public class StandardTrailerTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getTagIndexResolver() {
        return new StandardTrailerTagIndexResolver();
    }

    @Override
    public int[] getAllTags() {
        return Tags.getTrailers();
    }
}
