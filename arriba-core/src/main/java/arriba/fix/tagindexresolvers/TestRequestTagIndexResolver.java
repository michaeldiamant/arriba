package arriba.fix.tagindexresolvers;

import arriba.fix.Tags;

public final class TestRequestTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.TEST_REQUEST_ID
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
