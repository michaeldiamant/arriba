package arriba.fix.inbound.messages;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class ResendRequest extends InboundFixMessage {

    public ResendRequest(final FixChunk headerChunk,
            final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getBeginSequenceNumber() {
        return this.getBodyValue(Tags.BEGIN_SEQUENCE_NUMBER);
    }

    public String getEndSequenceNumber() {
        return this.getBodyValue(Tags.END_SEQUENCE_NUMBER);
    }
}
