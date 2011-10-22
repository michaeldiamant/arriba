package arriba.fix.chunk.arrays;

import java.util.Arrays;

import arriba.fix.Tags;

public final class StandardHeaderTagIndexResolver implements TagIndexResolver {

    // TODO Consider adding initialization check to ensure all header tags
    // have been used (e.g. test against Tags.getHeaders()).

    private static final int MAX_TAG = Tags.TARGET_COMP_ID;
    private static final int[] TAG_INDEXES = new int[MAX_TAG + 1];
    private static int TAG_COUNT = 0;

    static {
        initializeTagIndexes();
    }

    private static void initializeTagIndexes() {
        Arrays.fill(TAG_INDEXES, TagIndexResolver.INVALID_TAG_INDEX);

        TAG_INDEXES[Tags.BEGIN_STRING] = TAG_COUNT++;
        TAG_INDEXES[Tags.BODY_LENGTH] = TAG_COUNT++;
        TAG_INDEXES[Tags.MESSAGE_SEQUENCE_NUMBER] = TAG_COUNT++;
        TAG_INDEXES[Tags.MESSAGE_TYPE] = TAG_COUNT++;
        TAG_INDEXES[Tags.SENDER_COMP_ID] = TAG_COUNT++;
        TAG_INDEXES[Tags.SENDING_TIME] = TAG_COUNT++;
        TAG_INDEXES[Tags.TARGET_COMP_ID] = TAG_COUNT++;
    }

    @Override
    public boolean isDefinedFor(final int tag) {
        return TAG_INDEXES[tag] != TagIndexResolver.INVALID_TAG_INDEX;
    }

    @Override
    public int getTagIndex(final int tag) {
        return TAG_INDEXES[tag];
    }

    @Override
    public int getTagCount() {
        return TAG_COUNT;
    }
}
