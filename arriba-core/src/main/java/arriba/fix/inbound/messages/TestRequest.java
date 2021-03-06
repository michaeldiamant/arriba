package arriba.fix.inbound.messages;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class TestRequest extends InboundFixMessage {

    public TestRequest(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getTestRequestId() {
        return this.getBodyValue(Tags.TEST_REQUEST_ID);
    }
}
