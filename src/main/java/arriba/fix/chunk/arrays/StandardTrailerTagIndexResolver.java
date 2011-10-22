package arriba.fix.chunk.arrays;

import arriba.fix.Tags;

public final class StandardTrailerTagIndexResolver extends TagIndexResolver {

    @Override
    protected int[] getRequiredTags() {
        return new int[] {
                Tags.CHECKSUM
        };
    }

    @Override
    protected int[] getOptionalTags() {
        return new int[0];
    }
}
