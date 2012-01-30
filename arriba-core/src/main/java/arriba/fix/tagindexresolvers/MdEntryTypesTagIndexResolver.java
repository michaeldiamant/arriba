package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class MdEntryTypesTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.MD_ENTRY_TYPE,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
