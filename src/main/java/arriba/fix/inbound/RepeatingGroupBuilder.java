package arriba.fix.inbound;

import java.util.Arrays;

import arriba.fix.RepeatingGroups;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.arrays.ArrayFixChunkBuilder;
import arriba.fix.tagindexresolvers.StandardHeaderTagIndexResolver;

public final class RepeatingGroupBuilder {

    private static final int MAX_REPEATING_GROUP_COUNT = 10;
    private static final int MAX_FIELD_COUNT = 100;

    private final FixChunk[][] repeatingGroups = new FixChunk[MAX_REPEATING_GROUP_COUNT][];
    private final int[] tags = new int[MAX_FIELD_COUNT];
    private final byte[][] values = new byte[MAX_FIELD_COUNT][];

    // FIXME Provided TagIndexResolver is incorrect.  Supplied so unit tests will pass.
    private final FixChunkBuilder chunkBuilder = new ArrayFixChunkBuilder(new StandardHeaderTagIndexResolver());

    private int[] repeatingGroupTags = null;
    private int fieldsReadIndex = 0;
    private int fieldsWrittenIndex = 0;
    private int repeatingGroupsCreatedCount = 0;
    private final FixChunkBuilder builder = null; // TODO How will be FixChunkBuilderSupplier be provided?

    public RepeatingGroupBuilder setNumberOfRepeatingGroupsField(final int tag, final int count) {
        if (null == (this.repeatingGroupTags = RepeatingGroups.NUMBER_IN_GROUP_TAGS[tag])) {
            throw new IllegalArgumentException("Tag " + tag + " is not a repeating group count tag.");
        }

        this.buildGroup();

        this.repeatingGroups[this.repeatingGroupsCreatedCount] = new FixChunk[count];
        ++this.repeatingGroupsCreatedCount;

        return this;
    }

    private void buildGroup() {
        if (0 == this.fieldsReadIndex) {
            return;
        }

        final FixChunk[] groupChunks = this.repeatingGroups[this.repeatingGroupsCreatedCount - 1];
        int groupChunkIndex = 0;
        int firstRepeatingGroupTag = -1;

        while (this.fieldsWrittenIndex < this.fieldsReadIndex) {
            if (firstRepeatingGroupTag == -1) {
                firstRepeatingGroupTag = this.tags[this.fieldsWrittenIndex];
            } else if (firstRepeatingGroupTag == this.tags[this.fieldsWrittenIndex]) {
                groupChunks[groupChunkIndex] = this.chunkBuilder.build();
                this.chunkBuilder.clear();
                ++groupChunkIndex;
            }

            this.chunkBuilder.addField(this.tags[this.fieldsWrittenIndex], this.values[this.fieldsWrittenIndex]);
            // TODO Consider resetting the value of each tags / values index.

            ++this.fieldsWrittenIndex;
        }

        groupChunks[groupChunkIndex] = this.chunkBuilder.build();
        this.chunkBuilder.clear();
    }

    public RepeatingGroupBuilder addField(final int tag, final byte[] value) {
        if (Arrays.binarySearch(this.repeatingGroupTags, tag) < 0) {
            throw new IllegalArgumentException("Tag " + tag + " is not a known repeating group tag (" + this.repeatingGroupTags + ").");
        }

        this.tags[this.fieldsReadIndex] = tag;
        this.values[this.fieldsReadIndex] = value;
        ++this.fieldsReadIndex;

        return this;
    }

    public FixChunk[][] build() {
        this.buildGroup();

        // TODO Return deep copy.
        //        final FixChunk[][] repeatingGroupsCopy = new FixChunk[repeatingGroupsCreatedCount - 1][];

        return this.repeatingGroups;
    }

    public void clear() {
        this.fieldsReadIndex = 0;
        this.fieldsWrittenIndex = 0;
        this.repeatingGroupsCreatedCount = 0;
        this.repeatingGroupTags = null;
    }
}
