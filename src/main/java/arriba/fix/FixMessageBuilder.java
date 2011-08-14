package arriba.fix;

import java.util.Arrays;
import java.util.HashMap;

import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.FixMessageFactory;

public final class FixMessageBuilder<C extends FixChunk> {

    private static final int[] HEADER_TAGS = Tags.getHeaders();
    private static final int[] TRAILER_TAGS = Tags.getTrailers();

    private final FixChunkBuilder<C> headerChunkBuilder;
    private final FixChunkBuilder<C> bodyChunkBuilder;
    private final FixChunkBuilder<C> trailerChunkBuilder;

    private String messageType = "";

    public FixMessageBuilder(final FixChunkBuilder<C> headerChunkBuilder, final FixChunkBuilder<C> bodyChunkBuilder,
            final FixChunkBuilder<C> trailerChunkBuilder, final FixMessageFactory fixMessageFactory) {
        this.headerChunkBuilder = headerChunkBuilder;
        this.bodyChunkBuilder = bodyChunkBuilder;
        this.trailerChunkBuilder = trailerChunkBuilder;
    }

    public FixMessageBuilder<C> addField(final int tag, final String value) {
        if (Arrays.binarySearch(HEADER_TAGS, tag) >= 0) {
            this.headerChunkBuilder.addField(tag, value);
        } else if (Arrays.binarySearch(TRAILER_TAGS, tag) >= 0) {
            this.trailerChunkBuilder.addField(tag, value);
        } else {
            this.bodyChunkBuilder.addField(tag, value);
        }

        return this;
    }

    public FixMessageBuilder<C> setMessageType(final String messageType) {
        this.messageType = messageType;

        return this;
    }

    public FixMessage build() {
        final FixMessage fixMessage = FixMessageFactory.create(this.messageType, this.headerChunkBuilder.build(), this.bodyChunkBuilder.build(),
                this.trailerChunkBuilder.build(), new HashMap<Integer, FixChunk>());

        return fixMessage;
    }

    public void clear() {
        this.headerChunkBuilder.clear();
        this.bodyChunkBuilder.clear();
        this.trailerChunkBuilder.clear();
    }
}
