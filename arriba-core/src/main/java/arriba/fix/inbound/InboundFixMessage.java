package arriba.fix.inbound;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.session.SessionId;

public abstract class InboundFixMessage {

    private final FixChunk headerChunk;
    private final FixChunk bodyChunk;
    private final FixChunk trailerChunk;
    private final FixChunk[][] repeatingGroups;

    public InboundFixMessage(final FixChunk headerChunk, final FixChunk bodyChunk,
            final FixChunk trailerChunk, final FixChunk[][] repeatingGroups) {
        this.headerChunk = headerChunk;
        this.bodyChunk = bodyChunk;
        this.trailerChunk = trailerChunk;
        this.repeatingGroups = repeatingGroups;
    }

    public SessionId getSessionId() {
        // From inbound FIX message perspective, the TargetCompID is the session's SenderCompID and vice-versa.
        return new SessionId(this.getTargetCompId(), this.getSenderCompId());
    }

    public String getMessageType() {
        return this.getHeaderValue(Tags.MESSAGE_TYPE);
    }

    public String getSendingTime() {
        return this.getHeaderValue(Tags.SENDING_TIME);
    }

    public String getSenderCompId() {
        return this.getHeaderValue(Tags.SENDER_COMP_ID);
    }

    public String getTargetCompId() {
        return this.getHeaderValue(Tags.TARGET_COMP_ID);
    }

    public String getHeaderValue(final int tag) {
        return this.headerChunk.getValue(tag);
    }

    public boolean hasHeaderValue(final int tag) {
        return null != this.headerChunk.getValue(tag);
    }

    public String getBodyValue(final int tag) {
        return this.bodyChunk.getValue(tag);
    }

    public String getTrailerValue(final int tag) {
        return this.trailerChunk.getValue(tag);
    }

    public FixChunk[] getGroup(final int numberOfEntriesTag) {
        // TODO Optimize string to int conversion.
        final int repeatingGroupIndex = Integer.parseInt(this.bodyChunk.getValue(numberOfEntriesTag));

        return this.repeatingGroups[repeatingGroupIndex];
    }

    public String getValue(final int tag) {
        if (this.bodyChunk.isDefinedFor(tag)) {
            return this.getBodyValue(tag);
        } else if (this.headerChunk.isDefinedFor(tag)) {
            return this.getHeaderValue(tag);
        }

        return this.getTrailerValue(tag);
    }
}
