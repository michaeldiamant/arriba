package arriba.fix.tagindexresolvers;

import java.util.Arrays;

import com.google.common.primitives.Ints;

public abstract class TagIndexResolver {

    private final static int INVALID_TAG_INDEX = -1;

    private final int[] tagIndexes;
    private final int tagCount;
    private final int maxTag;

    public TagIndexResolver() {
        final int[] requiredTags = this.getRequiredTags();
        final int[] optionalTags = this.getOptionalTags();

        if (0 == requiredTags.length) {
            this.maxTag = Ints.max(optionalTags);
        } else if (0 == optionalTags.length) {
            this.maxTag = Ints.max(requiredTags);
        } else {
            this.maxTag = Ints.max(Ints.max(requiredTags), Ints.max(optionalTags));
        }

        this.tagIndexes = new int[this.maxTag + 1];
        this.tagCount = requiredTags.length + optionalTags.length;

        Arrays.fill(this.tagIndexes, INVALID_TAG_INDEX);
        this.populateTagIndexes(requiredTags, 0);
        this.populateTagIndexes(optionalTags, requiredTags.length);
    }

    private void populateTagIndexes(final int[] tags, int writeIndex) {
        for (final int tag : tags) {
            this.tagIndexes[tag] = writeIndex++;
        }
    }

    public boolean isDefinedFor(final int tag) {
        return tag <= this.maxTag &&
                this.getTagIndex(tag) != TagIndexResolver.INVALID_TAG_INDEX;
    }

    public int getTagIndex(final int tag) {
        return this.tagIndexes[tag];
    }

    public int getTagCount() {
        return this.tagCount;
    }

    protected abstract int[] getRequiredTags();

    protected abstract int[] getOptionalTags();
}
