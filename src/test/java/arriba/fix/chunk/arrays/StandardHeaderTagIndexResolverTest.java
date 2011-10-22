package arriba.fix.chunk.arrays;

import arriba.fix.Tags;

public class StandardHeaderTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getTagIndexResolver() {
        return new StandardHeaderTagIndexResolver();
    }

    @Override
    public int[] getAllTags() {
        return Tags.getHeaders();
    }
}
