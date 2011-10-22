package arriba.fix.chunk.arrays;

import java.util.Arrays;

import arriba.fix.Tags;

public final class NewOrderSingleTagIndexResolver extends TagIndexResolver {

    private static final int MAX_TAG = Tags.TRANSACTION_TIME;
    private static final int[] TAG_INDEXES = new int[MAX_TAG + 1];
    private static int TAG_COUNT = 0;

    static {
        initializeTagIndexes();
    }

    private static void initializeTagIndexes() {
        Arrays.fill(TAG_INDEXES, TagIndexResolver.INVALID_TAG_INDEX);

        TAG_INDEXES[Tags.CLIENT_ORDER_ID] = TAG_COUNT++;
        TAG_INDEXES[Tags.SYMBOL] = TAG_COUNT++;
        TAG_INDEXES[Tags.SIDE] = TAG_COUNT++;
        TAG_INDEXES[Tags.TRANSACTION_TIME] = TAG_COUNT++;
        TAG_INDEXES[Tags.ORDER_QUANTITY] = TAG_COUNT++;
        TAG_INDEXES[Tags.ORDER_TYPE] = TAG_COUNT++;

        // optional tags
        TAG_INDEXES[Tags.ACCOUNT] = TAG_COUNT++;
        TAG_INDEXES[Tags.PRICE] = TAG_COUNT++;
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
