package arriba.fix.inbound;

import java.util.Arrays;

import arriba.fix.RepeatingGroups;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.FixChunkBuilderSupplier;

public final class RepeatingGroupBuilder {

    private static final int MAX_REPEATING_GROUP_COUNT = 10;
    private static final int MAX_FIELD_COUNT = 100;
    private static final FixChunk END_OF_CHUNK = null;

    private final FixChunk[][] repeatingGroups = new FixChunk[MAX_REPEATING_GROUP_COUNT][];
    private final int[] numberOfRepeatingGroupTags = new int[MAX_REPEATING_GROUP_COUNT];

    private final int[] tags = new int[MAX_FIELD_COUNT];
    private final byte[][] values = new byte[MAX_FIELD_COUNT][];

    private final FixChunkBuilderSupplier supplier;

    private int[] repeatingGroupTags = null;
    private int fieldsReadIndex = 0;
    private int fieldsWrittenIndex = 0;
    private int repeatingGroupCount = -1;
    private FixChunkBuilder builder = null;

    public RepeatingGroupBuilder(final FixChunkBuilderSupplier supplier) {
        this.supplier = supplier;

        this.initializeRepeatingGroups();
    }

    private void initializeRepeatingGroups() {
        for (int repeatingGroupIndex = 0; repeatingGroupIndex < this.repeatingGroups.length; repeatingGroupIndex++) {
            this.repeatingGroups[repeatingGroupIndex] = new FixChunk[MAX_FIELD_COUNT];
        }
    }

    public RepeatingGroupBuilder setNumberOfRepeatingGroupsField(final int tag, final int count) {
        if (null == (this.repeatingGroupTags = RepeatingGroups.NUMBER_IN_GROUP_TAGS[tag])) {
            throw new IllegalArgumentException("Tag " + tag + " is not a repeating group count tag.");
        }

        this.buildGroup();
        this.updateBuilder(tag);

        ++this.repeatingGroupCount;
        this.repeatingGroups[this.repeatingGroupCount] = new FixChunk[count];
        this.numberOfRepeatingGroupTags[this.repeatingGroupCount] = tag;

        return this;
    }

    private void updateBuilder(final int numberOfRepeatingGroupsTag) {
        this.builder = this.supplier.getRepeatingGroupBuilder(numberOfRepeatingGroupsTag);

        if (null == this.builder) {
            throw new IllegalArgumentException("Did not find repeating group for tag " + numberOfRepeatingGroupsTag + ".");
        }
    }

    private void buildGroup() {
        if (0 == this.fieldsReadIndex) {
            return;
        }

        final FixChunk[] repeatingGroup = this.repeatingGroups[this.repeatingGroupCount];
        int repeatingGroupIndex = 0;
        int firstRepeatingGroupTag = -1;

        while (this.fieldsWrittenIndex < this.fieldsReadIndex) {
            if (firstRepeatingGroupTag == -1) {
                firstRepeatingGroupTag = this.tags[this.fieldsWrittenIndex];
            } else if (firstRepeatingGroupTag == this.tags[this.fieldsWrittenIndex]) {
                repeatingGroup[repeatingGroupIndex] = this.builder.build();
                this.builder.clear();
                ++repeatingGroupIndex;
            }

            this.builder.addField(this.tags[this.fieldsWrittenIndex], this.values[this.fieldsWrittenIndex]);
            // TODO Consider resetting the value of each tags / values index.

            ++this.fieldsWrittenIndex;
        }

        repeatingGroup[repeatingGroupIndex] = this.builder.build();
        repeatingGroup[++repeatingGroupIndex] = END_OF_CHUNK;
        this.builder.clear();
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

        return this.copyRepeatingGroups();
    }

    public int[] getNumberOfRepeatingGroupTags() {
        final int[] numberOfRepeatingGroupTagsCopy = new int[this.repeatingGroupCount];
        System.arraycopy(this.numberOfRepeatingGroupTags, 0, numberOfRepeatingGroupTagsCopy, 0, this.repeatingGroupCount);

        return numberOfRepeatingGroupTagsCopy;
    }

    private FixChunk[][] copyRepeatingGroups() {
        final FixChunk[][] repeatingGroupsCopy = new FixChunk[this.repeatingGroupCount][];
        for (final FixChunk[] repeatingGroup : this.repeatingGroups) {
            final int numberOfRepeatingGroups = this.getNumberOfRepeatingGroups(repeatingGroup);

            final FixChunk[] repeatingGroupCopy = new FixChunk[numberOfRepeatingGroups];
            System.arraycopy(repeatingGroup, 0, repeatingGroupCopy, 0, numberOfRepeatingGroups);
        }

        return repeatingGroupsCopy;
    }

    private int getNumberOfRepeatingGroups(final FixChunk[] repeatingGroup) {
        int groupIndex = 0;
        while(repeatingGroup[groupIndex++] != END_OF_CHUNK) {}

        return --groupIndex;
    }

    public void clear() {
        this.fieldsReadIndex = 0;
        this.fieldsWrittenIndex = 0;
        this.repeatingGroupCount = -1;
        this.repeatingGroupTags = null;
        this.builder = null;
    }
}
