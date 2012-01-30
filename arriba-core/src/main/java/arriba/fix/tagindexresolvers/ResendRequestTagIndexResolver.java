package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public class ResendRequestTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.BEGIN_SEQUENCE_NUMBER,
                Tags.END_SEQUENCE_NUMBER,
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
