package arriba.fix.inbound.messages;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;

public final class Logon extends InboundFixMessage {

    public Logon(final FixChunk headerChunk, final FixChunk bodyChunk, final FixChunk trailerChunk,
            final FixChunk[][] repeatingGroups) {
        super(headerChunk, bodyChunk, trailerChunk, repeatingGroups);
    }
    
    public String getEncryptMethod() {
        return this.getBodyValue(Tags.ENCRYPT_METHOD);
    }

    public String getUsername() {
        return this.getBodyValue(Tags.USERNAME);
    }

    public String getPassword() {
        return this.getBodyValue(Tags.PASSWORD);
    }

    public String getHeartbeatInterval() {
        return this.getBodyValue(Tags.HEARTBEAT_INTERVAL);
    }

    public String getResetSequenceNumberFlag() {
        return this.getBodyValue(Tags.RESET_SEQUENCE_NUMBER_FLAG);
    }
}
