package arriba.fix.inbound;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import arriba.fix.RepeatingGroups;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;

import com.google.common.collect.Maps;

public final class RepeatingGroupBuilder {

    private static final int MAX_REPEATING_GROUP_COUNT = 1000;

    private final int[] tags = new int[MAX_REPEATING_GROUP_COUNT];
    private final byte[][] values = new byte[MAX_REPEATING_GROUP_COUNT][];
    private final Map<Integer, FixChunk[]> groupCountToGroupChunk = Maps.newHashMap();
    private final FixChunkBuilder chunkBuilder = new ArrayFixChunkBuilder();

    private int[] repeatingGroupTags = null;
    private int tagValueIndex = 0;
    private int numberOfRepeatingGroupsTag;
    private int numberOfRepeatingGroups;
    private int builtTagIndex = 0;

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

        final FixChunk[] groupChunks = new FixChunk[this.numberOfRepeatingGroups];
        int groupChunkIndex = 0;
        int firstRepeatingGroupTag = -1;

        for (; this.builtTagIndex < this.tagValueIndex; this.builtTagIndex++) {
            if (firstRepeatingGroupTag == -1) {
                firstRepeatingGroupTag = this.tags[this.builtTagIndex];
            } else if (firstRepeatingGroupTag == this.tags[this.builtTagIndex]) {
                groupChunks[groupChunkIndex] = this.chunkBuilder.build();
                this.chunkBuilder.clear();
                ++groupChunkIndex;
            }

            this.chunkBuilder.addField(this.tags[this.builtTagIndex], this.values[this.builtTagIndex]);
            // TODO Consider resetting the value of each tags / values index.
        }

        groupChunks[groupChunkIndex] = this.chunkBuilder.build();
        this.chunkBuilder.clear();
        this.groupCountToGroupChunk.put(this.numberOfRepeatingGroupsTag, groupChunks);
    }

    @Deprecated
    public RepeatingGroupBuilder addField(final int tag, final String value) {
        return this.addField(tag, value.getBytes());
    }

    public RepeatingGroupBuilder addField(final int tag, final byte[] value) {
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
        this.builtTagIndex = 0;
        this.repeatingGroupTags = null;
        this.groupCountToGroupChunk.clear();
    }
}
