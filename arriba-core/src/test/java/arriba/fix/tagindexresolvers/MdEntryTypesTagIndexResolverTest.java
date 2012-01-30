package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class MdEntryTypesTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new MdEntryTypesTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return new int[] {
                Tags.MD_ENTRY_TYPE
        };
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[0];
    }
}
