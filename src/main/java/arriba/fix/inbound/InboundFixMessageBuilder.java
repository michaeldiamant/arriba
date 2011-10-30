package arriba.fix.inbound;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;

public final class InboundFixMessageBuilder {

    private static final int[] HEADER_TAGS = Tags.getHeaders();
    private static final int[] TRAILER_TAGS = Tags.getTrailers();

    private final FixChunkBuilder headerChunkBuilder;
    private final FixChunkBuilder bodyChunkBuilder;
    private final FixChunkBuilder trailerChunkBuilder;

    private Map<Integer, FixChunk[]> repeatingGroupTagToRepeatingGroups;

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

    public InboundFixMessageBuilder setRepeatingGroups(final Map<Integer, FixChunk[]> repeatingGroupTagToRepeatingGroups) {
        this.repeatingGroupTagToRepeatingGroups = repeatingGroupTagToRepeatingGroups;

        return this;
    }

    public InboundFixMessage build() {
        // TODO Check for existence of checksum and actually compute it.
        this.trailerChunkBuilder.addField(Tags.CHECKSUM, "1337".getBytes());

        final InboundFixMessage inboundFixMessage = InboundFixMessageFactory.create(
                this.headerChunkBuilder.build(), this.bodyChunkBuilder.build(), this.trailerChunkBuilder.build(),
                this.repeatingGroupTagToRepeatingGroups == null ? new HashMap<Integer, FixChunk[]>() : this.repeatingGroupTagToRepeatingGroups);

        return inboundFixMessage;
    }

    public void clear() {
        this.headerChunkBuilder.clear();
        this.bodyChunkBuilder.clear();
        this.trailerChunkBuilder.clear();
        this.repeatingGroupTagToRepeatingGroups = null;
    }
}
