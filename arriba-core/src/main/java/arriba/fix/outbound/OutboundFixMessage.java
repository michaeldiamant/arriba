package arriba.fix.outbound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import arriba.fix.Tags;

public final class OutboundFixMessage {

    private static final byte[] BEGIN_STRING_BYTES = Tags.toDelimitedByteArray(Tags.BEGIN_STRING);
    private static final byte[] BODY_LENGTH_BYTES = Tags.toDelimitedByteArray(Tags.BODY_LENGTH);
    private static final byte[] SENDING_TIME_BYTES = Tags.toDelimitedByteArray(Tags.SENDING_TIME);
    private static final byte[] MESSAGE_SEQUENCE_NUMBER_BYTES = Tags.toDelimitedByteArray(Tags.MESSAGE_SEQUENCE_NUMBER);
    private static final byte[] CHECKSUM_BYTES = Tags.toDelimitedByteArray(Tags.CHECKSUM);

    private final ByteArrayOutputStream headerOut;
    private final ByteArrayOutputStream bodyAndTrailerOut;
    private int messageBytesSum;
    private final String senderCompId;
    private final String targetCompId;
    private final String beginString;

    public OutboundFixMessage(final ByteArrayOutputStream headerOut, final ByteArrayOutputStream bodyAndTrailerOut, final int messageBytesSum,
            final String beginString, final String senderCompId, final String targetCompId) {
        this.headerOut = headerOut;
        this.bodyAndTrailerOut = bodyAndTrailerOut;
        this.messageBytesSum = messageBytesSum;
        this.beginString = beginString;
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }

    public String getSenderCompId() {
        return this.senderCompId;
    }

    public byte[] toBytes(final int messageSequenceNumber, final String sendingTime) {
        try {
            this.messageBytesSum += FieldWriter.write(MESSAGE_SEQUENCE_NUMBER_BYTES, Integer.toString(messageSequenceNumber), this.headerOut);
            this.messageBytesSum += FieldWriter.write(SENDING_TIME_BYTES, sendingTime, this.headerOut);

            final int bodyLength = this.bodyAndTrailerOut.size() + this.headerOut.size();

            final ByteArrayOutputStream finalOut = new ByteArrayOutputStream();
            FieldWriter.write(BEGIN_STRING_BYTES, this.beginString, finalOut);
            FieldWriter.write(BODY_LENGTH_BYTES, Integer.toString(bodyLength), finalOut);

            final int checksum = this.messageBytesSum % 256;
            // TODO Create lookup table.
            FieldWriter.write(CHECKSUM_BYTES, Integer.toString(checksum), this.bodyAndTrailerOut);

            finalOut.write(this.headerOut.toByteArray());
            finalOut.write(this.bodyAndTrailerOut.toByteArray());

            return finalOut.toByteArray();
        } catch (final IOException e) {
            return new byte[0];
        }
    }
}
