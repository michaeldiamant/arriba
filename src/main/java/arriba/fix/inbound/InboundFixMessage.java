package arriba.fix.inbound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import arriba.fix.Fields;
import arriba.fix.Tags;
import arriba.fix.chunk.FixChunk;
import arriba.fix.fields.BeginString;
import arriba.fix.session.SessionId;
import arriba.fix.session.SimpleSessionId;

public abstract class InboundFixMessage {

    private static final int DEFAULT_BYTE_ARRAY_SIZE = 32;

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
        return new SimpleSessionId(this.getSenderCompId());
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

    public byte[] toByteArray() {
        try {
            // TODO Create ByteArrayOutputStream implementation sans synchronization.
            final ByteArrayOutputStream bodyBytes = new ByteArrayOutputStream();
            //            writeBody(bodyBytes, this.bodyChunk, this.groupCountToGroupChunk);

            final ByteArrayOutputStream messageBytes =
                    new ByteArrayOutputStream(DEFAULT_BYTE_ARRAY_SIZE + bodyBytes.size());
            write(messageBytes, Tags.BEGIN_STRING, BeginString.FIXT11);
            // TODO Optimize integer serialization.
            write(messageBytes, Tags.BODY_LENGTH, String.valueOf(bodyBytes.size()).getBytes());

            this.headerChunk.write(messageBytes);
            // TODO Create ByteArrayOutputStream implementation that skips deep copy on toByteArray().
            // See http://javatechniques.com/blog/faster-deep-copies-of-java-objects/
            messageBytes.write(bodyBytes.toByteArray());
            this.trailerChunk.write(messageBytes);

            return messageBytes.toByteArray();
        } catch (final IOException e) {
            return new byte[0];
        }
    }

    private static void writeBody(final OutputStream bodyBytes, final FixChunk bodyChunk,
            final Map<Integer, FixChunk[]> groupCountToGroupChunk) throws IOException {
        bodyChunk.write(bodyBytes);

        for (final Entry<Integer, FixChunk[]> numberOfRepeatingGroupsTagToFixChunks : groupCountToGroupChunk.entrySet()) {
            final byte[] numberOfRepeatingGroupsBytes = String.valueOf(numberOfRepeatingGroupsTagToFixChunks.getValue().length).getBytes();
            write(bodyBytes, numberOfRepeatingGroupsTagToFixChunks.getKey(), numberOfRepeatingGroupsBytes);

            for (final FixChunk repeatingGroupChunk : numberOfRepeatingGroupsTagToFixChunks.getValue()) {
                repeatingGroupChunk.write(bodyBytes);
            }
        }
    }

    private static void write(final OutputStream outputStream, final int tag, final byte[] value) throws IOException {
        outputStream.write(Tags.toByteArray(tag));
        outputStream.write(Fields.EQUAL_SIGN);
        outputStream.write(value);
        outputStream.write(Fields.DELIMITER);
    }
}
