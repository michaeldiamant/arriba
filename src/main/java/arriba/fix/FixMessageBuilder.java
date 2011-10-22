package arriba.fix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import arriba.fix.chunk.FixChunk;
import arriba.fix.chunk.FixChunkBuilder;
import arriba.fix.fields.BeginString;
import arriba.fix.messages.FixMessage;
import arriba.fix.messages.FixMessageFactory;

public final class FixMessageBuilder {

    private static final byte[] DEFAULT_BEGIN_STRING_BYTES = BeginString.FIXT11;
    private static final int[] HEADER_TAGS = Tags.getHeaders();
    private static final int[] TRAILER_TAGS = Tags.getTrailers();

    private byte[] beginStringBytes = DEFAULT_BEGIN_STRING_BYTES;
    private final FixChunkBuilder headerChunkBuilder;
    private final FixChunkBuilder bodyChunkBuilder;
    private final FixChunkBuilder trailerChunkBuilder;

    private String messageType = "";
    private Map<Integer, FixChunk[]> repeatingGroupTagToRepeatingGroups;

    public FixMessageBuilder(final FixChunkBuilder headerChunkBuilder, final FixChunkBuilder bodyChunkBuilder,
            final FixChunkBuilder trailerChunkBuilder) {
        this.headerChunkBuilder = headerChunkBuilder;
        this.bodyChunkBuilder = bodyChunkBuilder;
        this.trailerChunkBuilder = trailerChunkBuilder;
    }

    public FixMessageBuilder addField(final int tag, final String value) {
        if (Arrays.binarySearch(HEADER_TAGS, tag) >= 0) {
            this.headerChunkBuilder.addField(tag, value);
        } else if (Arrays.binarySearch(TRAILER_TAGS, tag) >= 0) {
            this.trailerChunkBuilder.addField(tag, value);
        } else {
            this.bodyChunkBuilder.addField(tag, value);
        }

        return this;
    }

    public FixMessageBuilder setBeginStringBytes(final byte[] beginStringBytes) {
        this.beginStringBytes = beginStringBytes;

        return this;
    }

    public FixMessageBuilder setMessageType(final String messageType) {
        this.messageType = messageType;

        return this;
    }

    public FixMessageBuilder setRepeatingGroups(final Map<Integer, FixChunk[]> repeatingGroupTagToRepeatingGroups) {
        this.repeatingGroupTagToRepeatingGroups = repeatingGroupTagToRepeatingGroups;

        return this;
    }

    public FixMessage build() {
        // TODO Check for existence of checksum and actually compute it.
        this.trailerChunkBuilder.addField(Tags.CHECKSUM, "1337");

        // TODO Add messageType in more appropiate location.
        this.headerChunkBuilder.addField(Tags.MESSAGE_TYPE, this.messageType);


        final FixMessage fixMessage = FixMessageFactory.create(this.messageType, this.beginStringBytes,
                this.headerChunkBuilder.build(), this.bodyChunkBuilder.build(), this.trailerChunkBuilder.build(),
                this.repeatingGroupTagToRepeatingGroups == null ? new HashMap<Integer, FixChunk[]>() : this.repeatingGroupTagToRepeatingGroups);

        return fixMessage;
    }

    public void clear() {
        this.beginStringBytes = DEFAULT_BEGIN_STRING_BYTES;
        this.headerChunkBuilder.clear();
        this.bodyChunkBuilder.clear();
        this.trailerChunkBuilder.clear();
        this.repeatingGroupTagToRepeatingGroups = null;
    }
}
