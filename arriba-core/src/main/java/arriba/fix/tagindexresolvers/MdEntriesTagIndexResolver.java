package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class MdEntriesTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.MD_ENTRY_TYPE,
                Tags.MD_ENTRY_PRICE,
                Tags.MD_ENTRY_SIZE,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
