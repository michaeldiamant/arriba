package arriba.fix.inbound.messages;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class SequenceReset extends InboundFixMessage {

    protected SequenceReset(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getGapFillFlag() {
        return this.getBodyValue(Tags.GAP_FILL_FLAG);
    }

    public String getNewSequenceNumber() {
        return this.getBodyValue(Tags.NEW_SEQUENCE_NUMBER);
    }
}
