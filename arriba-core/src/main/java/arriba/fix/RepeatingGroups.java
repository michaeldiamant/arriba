package arriba.fix;

import java.util.Arrays;

public final class RepeatingGroups {

    private static final int MAXIMUM_TAG = 1000;

    public static final int[][] NUMBER_IN_GROUP_TAGS = new int[MAXIMUM_TAG][];

    static {
        populateNumberInGroupTags();
        sortNumberInGroupTagValues();
    }

    private static void populateNumberInGroupTags() {
        NUMBER_IN_GROUP_TAGS[Tags.NUMBER_MD_ENTRIES] = new int[] {
                Tags.MD_ENTRY_PRICE,
                Tags.MD_ENTRY_SIZE,
                Tags.MD_ENTRY_TYPE
        };

        NUMBER_IN_GROUP_TAGS[Tags.NUMBER_RELATED_SYMBOLS] = new int[] {
                Tags.SYMBOL
        };

        NUMBER_IN_GROUP_TAGS[Tags.NUMBER_MD_ENTRY_TYPES] = new int[] {
                Tags.MD_ENTRY_TYPE
        };
    }

    private static void sortNumberInGroupTagValues() {
        for (final int[] repeatingGroupTags : NUMBER_IN_GROUP_TAGS) {
            if (null != repeatingGroupTags) {
                Arrays.sort(repeatingGroupTags);
            }
        }
    }
}
