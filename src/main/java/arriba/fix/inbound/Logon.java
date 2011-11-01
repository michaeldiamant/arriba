package arriba.fix.inbound;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class Logon extends InboundFixMessage {

    public Logon(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }

    public String getUsername() {
        return this.getBodyValue(Tags.USERNAME);
    }

    public String getPassword() {
        return this.getBodyValue(Tags.PASSWORD);
    }
}
