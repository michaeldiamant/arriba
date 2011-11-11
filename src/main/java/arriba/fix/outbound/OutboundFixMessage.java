package arriba.fix.outbound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import arriba.fix.Tags;

public final class OutboundFixMessage {

    private static final byte[] SENDING_TIME_BYTES = Tags.toDelimitedByteArray(Tags.SENDING_TIME);
    private static final byte[] MESSAGE_SEQUENCE_NUMBER_BYTES = Tags.toDelimitedByteArray(Tags.MESSAGE_SEQUENCE_NUMBER);
    private static final byte[] CHECKSUM_BYTES = Tags.toDelimitedByteArray(Tags.CHECKSUM);

    private final ByteArrayOutputStream headerOut;
    private final ByteArrayOutputStream nonHeaderOut;
    private int messageBytesSum;
    private final String senderCompId;
    private final String targetCompId;

    public OutboundFixMessage(final ByteArrayOutputStream headerOut, final ByteArrayOutputStream nonHeaderOut,
            final int messageBytesSum, final String senderCompId, final String targetCompId) {
        this.headerOut = headerOut;
        this.nonHeaderOut = nonHeaderOut;
        this.messageBytesSum = messageBytesSum;
        this.senderCompId = senderCompId;
        this.targetCompId = targetCompId;
    }

    public String getTargetCompId() {
        return this.targetCompId;
    }

    public String getSenderCompId() {
        return this.getSenderCompId();
    }

    public byte[] toBytes(final int messageSequenceNumber, final String sendingTime) {
        try {
            this.messageBytesSum += FieldWriter.write(MESSAGE_SEQUENCE_NUMBER_BYTES, Integer.toString(messageSequenceNumber), this.headerOut);
            this.messageBytesSum += FieldWriter.write(SENDING_TIME_BYTES, sendingTime, this.headerOut);

            final int checksum = this.messageBytesSum % 256;
            // TODO Create lookup table.
            FieldWriter.write(CHECKSUM_BYTES, Integer.toString(checksum), this.nonHeaderOut);

            this.headerOut.write(this.nonHeaderOut.toByteArray());
        } catch (final IOException e) {}

        return this.headerOut.toByteArray();
    }
}
