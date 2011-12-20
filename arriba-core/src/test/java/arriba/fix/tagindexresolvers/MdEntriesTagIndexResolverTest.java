package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class MdEntriesTagIndexResolverTest extends AbstractTagIndexResolverTest {

    @Override
    public TagIndexResolver getResolver() {
        return new MdEntriesTagIndexResolver();
    }

    @Override
    public int[] getExpectedRequiredTags() {
        return new int[] {
                Tags.MD_ENTRY_TYPE,
                Tags.MD_ENTRY_PRICE,
                Tags.MD_ENTRY_SIZE,
        };
    }

    @Override
    public int[] getExpectedOptionalTags() {
        return new int[0];
    }
}
