package arriba.transport.netty;

import java.util.Arrays;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import arriba.fix.Fields;
import arriba.fix.Tags;

public final class FixMessageFrameDecoder extends FrameDecoder {

    private static final byte[] CHECKSUM_BYTES = Tags.toByteArray(Tags.CHECKSUM);
    private static final int DELIMITER_LENGTH = 1;

    private final ChannelBuffer[] decodedMessages = new ChannelBuffer[100];

    private int decodedMessageCount = 0;
    private byte nextFlagByte;
    private int nextFlagIndex;
    private boolean hasFoundFinalDelimiter;

    public FixMessageFrameDecoder() {
        this.reset();
    }

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        ChannelBuffer message = null;
        while ((message = this.decodeMessage(buffer)) != null) {
            this.decodedMessages[this.decodedMessageCount++] = message;
        }

        if (this.decodedMessageCount == 0) {
            return null;
        }

        final ChannelBuffer[] decodedMessagesCopy = new ChannelBuffer[this.decodedMessageCount];
        System.arraycopy(this.decodedMessages, 0, decodedMessagesCopy, 0, this.decodedMessageCount);
        this.decodedMessageCount = 0;

        return decodedMessagesCopy;
    }

    private ChannelBuffer decodeMessage(final ChannelBuffer buffer) {
        // TODO Should verify that tags are being received in correct order (e.g. checksum is last tag).
        // It is currently being assumed that tags are in the proper order.

        // Maintain the original buffer reader index to calculate the byte[] size when a complete FIX message is found.

        final int bufferStartReadIndex = buffer.readerIndex();

        final String bufferString = new String(buffer.array());
        System.out.println("Received buffer " + bufferString);

        buffer.markReaderIndex();

        while ((this.nextFlagIndex = buffer.bytesBefore(this.nextFlagByte)) != -1) {

            if (Fields.EQUAL_SIGN == this.nextFlagByte) {
                this.nextFlagByte = Fields.DELIMITER;

                final byte[] tag = buffer.readBytes(this.nextFlagIndex).array();
                buffer.skipBytes(DELIMITER_LENGTH);

                if (Arrays.equals(CHECKSUM_BYTES, tag)) {
                    this.hasFoundFinalDelimiter = true;
                }
            } else if (Fields.DELIMITER == this.nextFlagByte) {
                this.nextFlagByte = Fields.EQUAL_SIGN;

                buffer.skipBytes(this.nextFlagIndex + DELIMITER_LENGTH);

                if (this.hasFoundFinalDelimiter) {
                    this.hasFoundFinalDelimiter = false;

                    final byte[] fixMessageBytes = new byte[buffer.readerIndex() - bufferStartReadIndex];
                    System.arraycopy(buffer.array(), bufferStartReadIndex, fixMessageBytes, 0, fixMessageBytes.length);

                    this.reset();

                    return ChannelBuffers.copiedBuffer(fixMessageBytes);
                }
            }
        }

        // Reset reader index so Netty will add new bytes to existing buffer until complete FIX message is received.
        buffer.resetReaderIndex();

        return null;
    }

    @Override
    protected Object decodeLast(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) throws Exception {
        // TODO Should probably be doing something here.
        System.out.println("decodelast called");
        return null;
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);

        e.getCause().printStackTrace();
    }

    private void reset() {
        this.nextFlagIndex = -1;
        this.nextFlagByte = Fields.EQUAL_SIGN;
        this.hasFoundFinalDelimiter = false;
    }
}
