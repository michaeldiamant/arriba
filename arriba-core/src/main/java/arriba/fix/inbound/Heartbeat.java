package arriba.fix.inbound;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class Heartbeat extends InboundFixMessage {

    public Heartbeat(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getTestRequestId() {
        return this.getBodyValue(Tags.TEST_REQUEST_ID);
    }
}
