package arriba.fix.inbound.messages;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class Logout extends InboundFixMessage {

    public Logout(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getText() {
        return this.getBodyValue(Tags.TEXT);
    }
}
