package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class LogonTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        // TODO Add required tags as per FIX specification.
        return new int[0];
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[] {
                Tags.USERNAME,
                Tags.PASSWORD
        };
    }
}
