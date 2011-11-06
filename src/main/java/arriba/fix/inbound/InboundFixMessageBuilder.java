package arriba.fix.inbound;

import java.util.Arrays;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.chunk.FixChunkBuilderSupplier;

public final class InboundFixMessageBuilder {

    private static final int[] HEADER_TAGS = Tags.getHeaders();
    private static final int[] TRAILER_TAGS = Tags.getTrailers();

    private final InboundFixMessageFactory factory;
    private final FixChunkBuilderSupplier supplier;
    private final FixChunkBuilder headerChunkBuilder;
    private final FixChunkBuilder trailerChunkBuilder;

    private FixChunkBuilder bodyChunkBuilder;

    public InboundFixMessageBuilder(final FixChunkBuilderSupplier supplier, final InboundFixMessageFactory factory) {
        this.supplier = supplier;
        this.headerChunkBuilder = this.supplier.getHeaderBuilder();
        this.trailerChunkBuilder = this.supplier.getTrailerBuilder();
        this.factory = factory;
    }

    public InboundFixMessageBuilder addField(final int tag, final byte[] value) {
        // TODO Make search constant time.
        if (Arrays.binarySearch(HEADER_TAGS, tag) >= 0) {
            this.headerChunkBuilder.addField(tag, value);
        } else if (Arrays.binarySearch(TRAILER_TAGS, tag) >= 0) {
            this.trailerChunkBuilder.addField(tag, value);
        } else {
            if (Tags.MESSAGE_TYPE == tag) {
                this.bodyChunkBuilder = this.supplier.getBodyBuilder(value);
            }

            this.bodyChunkBuilder.addField(tag, value);
        }

        return this;
    }

    public InboundFixMessage build(final FixChunk[][] repeatingGroups, final int[] numberOfRepeatingGroupTags) {
        this.populateNumberOfRepeatingGroupTagMappings(numberOfRepeatingGroupTags);

        // TODO Check for existence of checksum and actually compute it.
        this.trailerChunkBuilder.addField(Tags.CHECKSUM, "1337".getBytes());

        final InboundFixMessage inboundFixMessage = this.factory.create(this.headerChunkBuilder.build(), this.bodyChunkBuilder
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
        this.trailerChunkBuilder.clear();
        this.bodyChunkBuilder.clear();
    }
}
