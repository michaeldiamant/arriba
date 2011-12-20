package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;
import arriba.fix.tagindexresolvers.StandardHeaderTagIndexResolver;
import arriba.fix.tagindexresolvers.TagIndexResolver;

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
