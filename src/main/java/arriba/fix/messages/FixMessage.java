package arriba.fix.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.session.SessionId;

public abstract class FixMessage {

    private final byte[] beginStringBytes;
    private final FixChunk headerChunk;
    private final FixChunk bodyChunk;
    private final FixChunk trailerChunk;
    private final Map<Integer, FixChunk[]> groupCountToGroupChunk;

    public FixMessage(final byte[] beginStringBytes, final FixChunk headerChunk, final FixChunk bodyChunk,
            final FixChunk trailerChunk, final Map<Integer, FixChunk[]> groupCountToGroupChunk) {
        this.beginStringBytes = beginStringBytes;
        this.headerChunk = headerChunk;
        this.bodyChunk = bodyChunk;
        this.trailerChunk = trailerChunk;
        this.groupCountToGroupChunk = groupCountToGroupChunk;
    }

    public SessionId getSessionId() {
        return null;
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

    public String getBodyValue(final int tag) {
        return this.bodyChunk.getValue(tag);
    }

    public String getTrailerValue(final int tag) {
        return this.trailerChunk.getValue(tag);
    }

    public FixChunk[] getGroup(final int numberOfEntriesTag) {
        return this.groupCountToGroupChunk.get(numberOfEntriesTag);
    }

    public String getValue(final int tag) {
        final String headerValue = this.getHeaderValue(tag);
        if (!headerValue.isEmpty()) {
            return headerValue;
        }

        final String bodyValue = this.getBodyValue(tag);
        if (!bodyValue.isEmpty()) {
            return bodyValue;
        }

        return this.getTrailerValue(tag);
    }

    public byte[] toByteArray() {
        final ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        try {
            messageBytes.write(Tags.BEGIN_STRING);
            messageBytes.write(this.beginStringBytes);

            // TODO Write out BodyLength field.

            this.headerChunk.write(messageBytes);
            this.bodyChunk.write(messageBytes);
            for (final FixChunk[] repeatingGroupChunks : this.groupCountToGroupChunk.values()) {
                for (final FixChunk repeatingGroupChunk : repeatingGroupChunks) {
                    repeatingGroupChunk.write(messageBytes);
                }
            }
            this.trailerChunk.write(messageBytes);

            return messageBytes.toByteArray();
        } catch (final IOException e) {
            return new byte[0];
        }
    }
}
