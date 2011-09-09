package arriba.fix.messages;

import java.util.Map;

import arriba.fix.chunk.FixChunk;

public final class Logon extends FixMessage {

    public Logon(final byte[] beginStringBytes, final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) {
        super(beginStringBytes, headerChunk, bodyChunk, trailerChunk, groupCountToGroupChunk);
    }
}
