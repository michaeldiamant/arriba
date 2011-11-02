package arriba.fix.inbound;

import java.util.Arrays;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;

public final class InboundFixMessageBuilder {

    private static final int[] HEADER_TAGS = Tags.getHeaders();
    private static final int[] TRAILER_TAGS = Tags.getTrailers();

    private final FixChunkBuilder headerChunkBuilder;
    private final FixChunkBuilder bodyChunkBuilder;
    private final FixChunkBuilder trailerChunkBuilder;

    public InboundFixMessageBuilder(final FixChunkBuilder headerChunkBuilder, final FixChunkBuilder bodyChunkBuilder,
            final FixChunkBuilder trailerChunkBuilder) {
        this.headerChunkBuilder = headerChunkBuilder;
        this.bodyChunkBuilder = bodyChunkBuilder;
        this.trailerChunkBuilder = trailerChunkBuilder;
    }

    public InboundFixMessageBuilder addField(final int tag, final byte[] value) {
        // TODO Make search constant time.

        if (Arrays.binarySearch(HEADER_TAGS, tag) >= 0) {
            this.headerChunkBuilder.addField(tag, value);
        } else if (Arrays.binarySearch(TRAILER_TAGS, tag) >= 0) {
            this.trailerChunkBuilder.addField(tag, value);
        } else {
            this.bodyChunkBuilder.addField(tag, value);
        }

        return this;
    }

    public InboundFixMessage build(final FixChunk[][] repeatingGroups, final int[] numberOfRepeatingGroupTags) {
        this.populateNumberOfRepeatingGroupTagMappings(numberOfRepeatingGroupTags);

        // TODO Check for existence of checksum and actually compute it.
        this.trailerChunkBuilder.addField(Tags.CHECKSUM, "1337".getBytes());

        final InboundFixMessage inboundFixMessage = InboundFixMessageFactory.create(this.headerChunkBuilder.build(), this.bodyChunkBuilder
                .build(), this.trailerChunkBuilder.build(), repeatingGroups);

        return inboundFixMessage;
    }

    private void populateNumberOfRepeatingGroupTagMappings(final int[] numberOfRepeatingGroupTags) {
        byte[] repeatingGroupIndex = null;
        for (int tagsIndex = 0; tagsIndex < numberOfRepeatingGroupTags.length; tagsIndex++) {
            repeatingGroupIndex = String.valueOf(tagsIndex).getBytes();
            this.addField(numberOfRepeatingGroupTags[tagsIndex], repeatingGroupIndex);
        }
    }

    public InboundFixMessage build() {
        return this.build(null, null);
    }

    public void clear() {
        this.headerChunkBuilder.clear();
        this.bodyChunkBuilder.clear();
        this.trailerChunkBuilder.clear();
    }
}
