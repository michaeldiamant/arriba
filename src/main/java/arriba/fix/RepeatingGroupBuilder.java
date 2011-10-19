package arriba.fix;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.arrays.ArrayFixChunk;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;

import com.google.common.collect.Maps;

public final class RepeatingGroupBuilder {

    private static final int MAX_REPEATING_GROUP_COUNT = 1000;

    private final int[] tags = new int[MAX_REPEATING_GROUP_COUNT];
    private final String[] values = new String[MAX_REPEATING_GROUP_COUNT];
    private final Map<Integer, FixChunk[]> groupCountToGroupChunk = Maps.newHashMap();
    private final FixChunkBuilder<ArrayFixChunk> chunkBuilder = new ArrayFixChunkBuilder();

    private int[] repeatingGroupTags = null;
    private int tagValueIndex = 0;
    private int numberOfRepeatingGroupsTag;
    private int numberOfRepeatingGroups;

    public RepeatingGroupBuilder setNumberOfRepeatingGroupsTag(final int tag) {
        if (null == (this.repeatingGroupTags = RepeatingGroups.NUMBER_IN_GROUP_TAGS[tag])) {
            throw new IllegalArgumentException("Tag " + tag + " is not a repeating group count tag.");
        }

        this.numberOfRepeatingGroupsTag = tag;

        return this;
    }

    public RepeatingGroupBuilder setNumberOfRepeatingGroups(final int value) {
        this.buildGroup();

        this.numberOfRepeatingGroups = value;

        return this;
    }

    private void buildGroup() {
        if (0 == this.tagValueIndex) {
            return;
        }

        // FIXME buildGroup() algorithm is incorrect.  Need to debug.

        final FixChunk[] groupChunks = new FixChunk[this.numberOfRepeatingGroups];
        int groupChunkIndex = 0;
        final int firstRepeatingGroupTag = this.tags[0];

        for (int localTagValueIndex = 0; localTagValueIndex < this.tagValueIndex; localTagValueIndex++) {
            if (localTagValueIndex > 0 && firstRepeatingGroupTag == this.tags[localTagValueIndex]) {
                groupChunks[groupChunkIndex] = this.chunkBuilder.build();
                this.chunkBuilder.clear();
                ++groupChunkIndex;
            }

            this.chunkBuilder.addField(this.tags[localTagValueIndex], this.values[localTagValueIndex]);
            // TODO Consider resetting the value of each tags / values index.

            ++localTagValueIndex;
        }

        this.groupCountToGroupChunk.put(this.numberOfRepeatingGroupsTag, groupChunks);
    }

    public RepeatingGroupBuilder addField(final int tag, final String value) {
        if (Arrays.binarySearch(this.repeatingGroupTags, tag) < 0) {
            throw new IllegalArgumentException("Tag " + tag + " is not a known repeating group tag (" + this.repeatingGroupTags + ").");
        }

        this.tags[this.tagValueIndex] = tag;
        this.values[this.tagValueIndex] = value;
        ++this.tagValueIndex;

        return this;
    }

    public Map<Integer, FixChunk[]> build() {
        this.buildGroup();

        final Map<Integer, FixChunk[]> mapToReturn =
                Collections.unmodifiableMap(Maps.newHashMap(this.groupCountToGroupChunk));

        return mapToReturn;
    }

    public void clear() {
        this.tagValueIndex = 0;
        this.numberOfRepeatingGroupsTag = 0;
        this.numberOfRepeatingGroups = 0;
        this.repeatingGroupTags = null;
        this.groupCountToGroupChunk.clear();
    }
}
