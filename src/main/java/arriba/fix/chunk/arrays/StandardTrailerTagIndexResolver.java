package arriba.fix.chunk.arrays;

import java.util.Arrays;

import arriba.fix.Tags;

public final class StandardTrailerTagIndexResolver extends TagIndexResolver {

    private static final int MAX_TAG = Tags.CHECKSUM;
    private static final int[] TAG_INDEXES = new int[MAX_TAG + 1];
    private static int TAG_COUNT = 0;

    static {
        initializeTagIndexes();
    }

    private static void initializeTagIndexes() {
        Arrays.fill(TAG_INDEXES, TagIndexResolver.INVALID_TAG_INDEX);

        TAG_INDEXES[Tags.CHECKSUM] = TAG_COUNT++;
    }

    @Override
    public int getTagIndex(final int tag) {
        return TAG_INDEXES[tag];
    }

    @Override
    public int getTagCount() {
        return TAG_COUNT;
    }

    @Override
    protected int getMaxTag() {
        return MAX_TAG;
    }
}
