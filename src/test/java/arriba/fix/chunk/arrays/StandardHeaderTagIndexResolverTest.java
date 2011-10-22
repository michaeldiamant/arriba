package arriba.fix.chunk.arrays;

import arriba.fix.Tags;

public class StandardHeaderTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new StandardHeaderTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return Tags.getHeaders();
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[0];
    }
}
